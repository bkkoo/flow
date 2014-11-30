package net.shantitree.flow.dbsync.job

import akka.actor.Actor
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.job.{AnotherJobFinished, BeginSync, DelayTimeout}
import net.shantitree.flow.dbsync.session.{JobRunnerRef, SyncSession}
import org.joda.time.DateTime

import scala.concurrent.duration._

trait TDelayRunningUtil { this: Actor =>
  import context._

  protected var jobDeadline:Deadline = 0.seconds.fromNow
  val syncSession: SyncSession

  def delayRunning(msg: Any): Unit = {
    system.scheduler.scheduleOnce(getJobRepeatDelay, self, DelayTimeout(msg))
  }

  def delayBeginSync(slog: SyncLog): Unit = {
    system.scheduler.scheduleOnce(getJobRepeatDelay, new Runnable {
      def run(): Unit = {
        val msg = BeginSync( slog.copy(start_at = DateTime.now.toDate) )
        self ! DelayTimeout(msg)
      }
    })
  }


  def runOnOverdue(slog: SyncLog)(fn: SyncLog => Any): Unit = {
    if (jobDeadline.isOverdue()) {
      fn( slog.copy( start_at = DateTime.now.toDate ) )
      jobDeadline = getJobRepeatDelay.fromNow
    } else {
      jobDeferred(slog)
    }
  }

  def waitFor(runnerNames: String*)(callback: List[AnotherJobFinished] => Unit): Unit = {
    val isRunnerName = runnerNames.toSet
    val runners = syncSession.runners.filter{ r => isRunnerName(r.name)}
    if (runners.isEmpty) {
      throw new RuntimeException(s"Can't wait for runners that are not in syncSession! runners = ${runnerNames.mkString(",")} ")
    } else {
      setDeferred(callback, runners)
    }
  }

  def setDeferred(callback: List[AnotherJobFinished]=>Unit, runners: Set[JobRunnerRef]):Unit
  def jobDeferred(slog: SyncLog): Unit
  def getJobRepeatDelay: FiniteDuration

}
