package net.shantitree.flow.dbsync.model

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.conversion.TGetArgValByName._
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._
import net.shantitree.flow.slick.qry.param.CreatedDuration
import net.shantitree.flow.dbsync.job.SyncJobConfig
import net.shantitree.flow.dbsync.model.SyncLog.{Fields, SResult, SType}
import org.joda.time.{DateTime, Duration => JDuration}
import net.shantitree.flow.sys.lib.model.conversion.ModelCtorArgs._
import java.util.Date

trait TSyncLogUtil {

  class Helper(slog: SyncLog) {

    def toCreatedDuration = {
      require(slog.from.nonEmpty && slog.until.nonEmpty)
      CreatedDuration(slog.from.get.toJoda, slog.until.get.toJoda)
    }

    def lastSyncUntilNow:JDuration = {
      val lastEndAt = slog.until.map(_.toJoda).getOrElse(DateTime.now)
      new JDuration(lastEndAt, DateTime.now)
    }

  }

  implicit def fromSLogToHelper(slog: SyncLog):Helper = new Helper(slog)

  def getNextSyncDurationParam(whenNoSyncInDays: Int, bulkPartitionByDays: Int , lastSyncUntil: DateTime):(Option[Date], Option[Date], String) = {

    val from = lastSyncUntil
    val now = DateTime.now
    val duration = new JDuration(lastSyncUntil, now).getStandardDays

    val (until, syncType) = if (duration <= 1 || duration < whenNoSyncInDays ) {
      (now, SType.Periodical )
    } else {
      val days = if (duration - bulkPartitionByDays > 0) {
        bulkPartitionByDays
      } else {
        /* Leaving a gap for 1 day to prevent 'until' value growing greater than
          the time 'now'! This help prevent next periodic sync from pulling empty
          record which can be possible when 'from' or 'until' value date time is
          in advance future time.
        */
        duration.toInt - 1
      }
      (from.plusDays(days), SType.Bulk)
    }

    (Some(from.toDate), Some(until.toDate), syncType)
  }

  def getNewSyncDurationLog(priorLog: SyncLog, config: SyncJobConfig): SyncLog = {

    if (priorLog.from.isEmpty || priorLog.until.isEmpty) {
      throw new IllegalArgumentException(
        s"Missing 'from' or 'until' value of prior log from database! prior log = $priorLog")
    }

    val now = DateTime.now
    val (pFrom, pUntil, pSyncType) = getNextSyncDurationParam(config.whenNoSyncInDays, config.bulkPartitionByDays, priorLog.until.get)

    priorLog.copy(
       from = pFrom
      ,until = pUntil
      ,sync_type = pSyncType
      ,start_at = now.toDate
      ,end_at = None
      ,result = SResult.Promise
      ,note = None
    )
    
  }
  
  def getNewSyncDurationLog(config: SyncJobConfig)(implicit g: OrientGraph): SyncLog = {

    val priorLog = findPriorLog(config.runnerName)

    priorLog.map { slog =>
      getNewSyncDurationLog(slog, config)
    } getOrElse {
      val (pFrom, pUntil, pSyncType) = getNextSyncDurationParam(config.whenNoSyncInDays, config.bulkPartitionByDays, config.initialDate.toDate)
      SyncLog( //prior log not found. A new slog is created with 'Bulk' sync type
        job_runner = config.runnerName
        ,from = pFrom
        ,until = pUntil
        ,result = SResult.Promise
        ,sync_type = pSyncType
      )
    }
  }

  def recordLog(slog: SyncLog)(implicit g: OrientGraph): SyncLog = {
    if (slog.result == SResult.Failure) {
      addNewLog(slog)
    } else {
      findSuccessLog(slog.job_runner) map { _.updateFrom(slog) } orElse { Some(addNewLog(slog)) }
      deleteFailureLog(slog.job_runner)
    }
    slog
  }

  def recordLogSuccess(slog: SyncLog)(implicit g: OrientGraph):SyncLog = {
    recordLog {
      slog.copy(
        result = SResult.Success
        ,end_at = Some(DateTime.now.toDate)
      )
    }
  }

  def recordLogDeferred(slog: SyncLog)(implicit g: OrientGraph):SyncLog = {
    recordLog {
      slog.copy(
        result = SResult.Success
        ,end_at = Some(DateTime.now.toDate)
        ,note = Some("deferred")
      )
    }
  }

  def recordLogFailure(slog: SyncLog, e:Throwable)(implicit g: OrientGraph):SyncLog = {
    recordLog {
      slog.copy(
         result = SResult.Failure
        ,note=Some(s"${e.getMessage}\r\n${e.getStackTrace}")
        ,end_at = Some(DateTime.now.toDate)
      )
    }
  }

  def addNewLog(slog: SyncLog)(implicit g: OrientGraph): SyncLog = {
    g.addV(slog)(SyncLog)
    slog
  }

  def deleteFailureLog(runnerName: String)(implicit g: OrientGraph): Option[Vertex] = {
    findFailureLog(runnerName) map { v =>
      v.remove()
      v
    }
  }

  def findPriorLog(runnerName: String)(implicit g: OrientGraph): Option[SyncLog] = {
    findSuccessLog(runnerName).map { v => from(v, SyncLog).createModel() }
  }
  
  def findLog(slog: SyncLog)(implicit g: OrientGraph): Option[Vertex]={
    slog.rid.map { 
      id => g.getVertex(id) 
    } orElse {
      findLogWithResult(slog.job_runner, slog.result)
    }
  }
  
  def findSuccessLog(runnerName:String)(implicit g: OrientGraph): Option[Vertex] ={
    findLogWithResult(runnerName, SResult.Success)
  }
  
  def findFailureLog(runnerName:String)(implicit g: OrientGraph): Option[Vertex] ={
    findLogWithResult(runnerName, SResult.Failure)
  }
  
  def findLogWithResult(runnerName: String, result: String)(implicit g: OrientGraph): Option[Vertex] = {
    val f = Fields
    g.findV[SyncLog](f.job_runner -> runnerName, f.result -> result)
  }

}
