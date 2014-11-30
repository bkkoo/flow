package net.shantitree.flow.dbsync.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.BaseQry
import net.shantitree.flow.dbsync.msg.job.{DelayTimeout, BeginSync}

trait TDelayRunningUtilRcv { this: JobRunner[_ <: BaseQry, _ <: TModel] =>
  import context._

  def rcvBeginSyncWithWaitForRunner(runnerName: String):Receive = partialRcv {

    case BeginSync(slog) => waitFor(runnerName) { _ =>
      system.log.info(s"Begin sync: ${runnerRef.name}")
      pull(slog)
    }

  }  orElse rcvDefault

  lazy val rcvBeginSyncWithDelayRunning:Receive = partialRcv {

    case BeginSync(slog) =>
      delayBeginSync(slog)
    case DelayTimeout(BeginSync(slog)) =>
      system.log.info(s"Begin sync: ${runnerRef.name}")
      pull(slog)

  } orElse rcvDefault

  lazy val rcvBeginSyncWithRunOnOverdue:Receive = partialRcv {

    case BeginSync(slog) =>
      runOnOverdue(slog) { newSlog =>
        system.log.info(s"Begin sync: ${runnerRef.name}")
        pull(newSlog)
      }

  } orElse rcvDefault

}
