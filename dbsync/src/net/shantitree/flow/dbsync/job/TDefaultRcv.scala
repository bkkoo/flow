package net.shantitree.flow.dbsync.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.BaseQry
import net.shantitree.flow.dbsync.model.SyncLog.SType
import net.shantitree.flow.dbsync.msg.job._

import scala.util.{Success, Failure}

trait TDefaultRcv { this: JobRunner[_<:BaseQry, _<:TModel] =>

  import context._

  lazy val rcvBeginSync:Receive = { case BeginSync(slog) =>
    system.log.info(s"Begin sync: ${runnerRef.name}")
    pull(slog)
  }
  lazy val rcvFailOnPulling:Receive = { case FailOnPulling(e, slog) => jobFail(e, slog) }
  lazy val rcvFailOnUpdating:Receive = {case FailOnUpdating(e, updater, slog) => jobFail(e, slog)}
  lazy val rcvOtherMsg: Receive = { case m => throw new RuntimeException(
    s"Receive unhandled Message '$m' in runnerRef '${runnerRef.name}'!")
  }

  lazy val rcvSessionFinished: Receive = {  case m:SessionFinished =>
    become(receive)
  }
  lazy val rcvAnotherJobFinished:Receive = { case m:AnotherJobFinished => dequeueDeferred(m) }
  lazy val rcvAnotherJobHasBeenDeferred:Receive = { case m:AnotherJobHasBeenDeferred => clearDeferred(m) }
  lazy val rcvExecInGraphSessionResult:Receive = {
    case ExecInGraphSessionResult(slog, Failure(e)) =>
      system.log.error(s"Execute in graph session failure with exception: $e. \r\nSLog: \r\n$slog")
      throw e
    case ExecInGraphSessionResult(slog, Success(_)) =>
  }
  lazy val rcvUpdateFinished:Receive = {
    case m@UpdateFinished(result, slog) => updateFinished(result, slog)
  }
  lazy val rcvPulledDatum:Receive = {
    case PulledDatum(datum,slog) =>
      syncMode match {
        case SType.Bulk =>
          update(datum, slog)
          pullNext()

        case SType.Periodical =>
          update(datum, slog)
      }
  }

  /*---------------------------------------------------------------------------------------------------*/

  lazy val rcvDefault:Receive =
      rcvBeginSync orElse
      rcvPulledDatum orElse
      rcvUpdateFinished orElse
      rcvAnotherJobFinished orElse
      rcvAnotherJobHasBeenDeferred orElse
      rcvExecInGraphSessionResult orElse
      rcvFailOnPulling orElse
      rcvFailOnUpdating orElse
      rcvSessionFinished orElse
      rcvOtherMsg

}
