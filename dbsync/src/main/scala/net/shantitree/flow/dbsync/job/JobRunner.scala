package net.shantitree.flow.dbsync.job

import akka.actor.{Actor, ActorRef}
import com.google.inject.Inject
import com.google.inject.name.Named
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.typesafe.config.Config
import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.BaseQry
import net.shantitree.flow.dbsync.model.SyncLog.SType
import net.shantitree.flow.dbsync.model.SyncLog.SType._
import net.shantitree.flow.dbsync.model.{SyncLogUtil, SyncLog}
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.dbsync.msg.result.{NoneResult, TSyncResult}
import net.shantitree.flow.dbsync.puller.TPuller
import net.shantitree.flow.dbsync.session.{DeferredUntilAnotherFinished, JobRunnerRef, SyncSession}
import net.shantitree.flow.sys.graph.GraphSession
import org.joda.time.DateTime
import scala.concurrent.duration.FiniteDuration
import SyncLogUtil._

abstract class JobRunner[Q <: BaseQry, M <: TModel](val job: Job[Q, M])
  extends Actor
  with TSyncLogRecordMan
  with TDelayRunningUtil
  with TDelayRunningUtilRcv
  with TDefaultRcv {

  import context._

  val runnerRef: JobRunnerRef = JobRunnerRef(self.path.name, self)

  private var deferred:DeferredUntilAnotherFinished = null

  private var _syncMode: String = null
  private var _syncSession:SyncSession = null
  private var _config: SyncJobConfig = _
  private var _dbSessionExec: ActorRef = _
  private var _graphSessionExec: ActorRef = _
  lazy val config = _config
  lazy val dbSessionExec = _dbSessionExec
  lazy val graphSessionExec = _graphSessionExec
  lazy val syncSession = _syncSession
  lazy val syncMode = _syncMode

  @Inject()
  def inject(@Named("DbSessionExec")dbSessionExec: ActorRef, @Named("GraphSessionExec")graphSessionExec:ActorRef, @Named("SyncJobConfig")syncJobConfig: Config) {
    this._dbSessionExec = dbSessionExec
    this._graphSessionExec = graphSessionExec
    this._config = SyncJobConfig(runnerRef.name, syncJobConfig)
  }

  
  def getPuller(slog: SyncLog): TPuller[Q]
  def periodical:Receive
  def bulk:Receive
  def partialRcv(rcv: Receive) = rcv
  def receive:Receive = rcvSessionBegin orElse rcvOtherMsg


  final lazy val rcvSessionBegin:Receive = {
    case SessionBegin(newSession) =>
      val slog = getSyncLogRecord().get

      _syncSession = newSession

      deferred = DeferredUntilAnotherFinished(runnerRef, _syncSession, slog)

      slog.sync_type match {
        case Bulk =>
          _syncMode = SType.Bulk
          become(bulk)
        case Periodical =>
          _syncMode = SType.Periodical
          become(periodical)
      }
      self ! BeginSync(slog)
  }

  def getJobRepeatDelay:FiniteDuration = _config.repeatDelay

  def pull(slog: SyncLog):Unit = { dbSessionExec ! PullRequest(getPuller(slog), slog) }

  def pullNext(): Boolean = {
    val priorLog = getSyncLogRecord().get
    if (priorLog.lastSyncUntilNow.getStandardDays > 1) {
      //leave a gap for 1 day to prevent 'until' be in future date time
      pull(nextSyncLogRecord().get)
      true
    } else {
      jobFinished(NoneResult, priorLog)
      false
    }
  }

  def update(datum: List[Product], slog: SyncLog): Boolean = {
    if (datum.nonEmpty) {
      val models = try {
        job.modelConverter(datum)
      } catch {
        case e:Exception =>
          jobFail(e, slog)
      }
      val updater = job.createUpdater(models)
      graphSessionExec ! RunUpdateInGraphSession(runnerRef, updater, slog)
      true
    } else {
      if (_syncMode == SType.Periodical) {
        nextSyncLogRecord(slog)
        jobFinished(NoneResult, slog.copy(result = "No new data", end_at = Some(DateTime.now.toDate)))
      }
      false
    }
  }

  def updateFinished(result: TSyncResult, slog: SyncLog): Unit = {
    system.log.info(s"Update finished: \r\n$slog ")
    syncMode match {
      case SType.Bulk =>
      case SType.Periodical =>
        nextSyncLogRecord(slog)
        jobFinished(result, slog)
    }
  }

  def execInGraphSession(slog:SyncLog)(fn:OrientGraph=>Any): Unit = {
    graphSessionExec ! ExecInGraphSession(runnerRef, slog)(fn)
  }

  def jobFail(e: Throwable, slog: SyncLog): Nothing = {
    system.log.error(e, s"Job Fail: \r\n $slog")
    GraphSession.tx { implicit g => SyncLogUtil.recordLogFailure(slog, e) }
    throw e
  }

  def jobDeferred(slog: SyncLog): Unit = {
    spanSyncLogRecord()
    _syncSession.ctrl ! JobDeferred(runnerRef, slog)
  }

  def jobFinished(result: TSyncResult, slog: SyncLog): Unit = {
    _syncSession.ctrl ! JobFinished(runnerRef, result, slog)
  }

  def setDeferred(callback: List[AnotherJobFinished]=>Unit, runners: Set[JobRunnerRef]):Unit = {
    deferred = deferred.copy(fn=Some(callback), waitForRunners = runners)
  }
  def clearDeferred(msg: AnotherJobHasBeenDeferred) = {
    val anotherRunner = msg.runner
    if (deferred.isWaitFor(anotherRunner)) {
      deferred = deferred.copy(waitForRunners = Set()) //clear
      jobDeferred(deferred.slog)
    }
  }
  def dequeueDeferred(msg: AnotherJobFinished) = {
    deferred = deferred.anotherJobFinished(msg)
    deferred.runWhenReady()
  }

}
