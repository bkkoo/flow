package net.shantitree.flow.dbsync.session

import akka.actor.Actor
import net.shantitree.flow.sys.lib.DataChangeEventBus
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.dbsync.job.SyncJobConfig
import net.shantitree.flow.dbsync.msg.job._
import org.joda.time.{Duration, DateTime}

trait TSyncSessionCtrl { this: Actor =>

  import context._

  val runners: Set[JobRunnerRef]
  var session:SyncSession = null
  var inQueue:Set[JobRunnerRef] = null
  private var sessionBeginAt: DateTime = null


  def createRunners(runnerNames: String*):Set[JobRunnerRef] = {
    runnerNames.map { n =>
      val actorRef = actorOf(Gak(system).props(n), name = n)
      JobRunnerRef(n, actorRef)
    }.toSet
  }

  def start():Unit = {
    session = SyncSession(runners, self)
    sessionBeginAt = DateTime.now
    inQueue = runners
    system.log.info(s"Sync session begin:  $sessionBeginAt  id: ${session.id}")
    runners.foreach { runner => runner.actorRef ! SessionBegin(session) }
  }

  def receive: Receive = {

    case Start =>
      start()

    case m@JobFinished(runner, result, slog) =>
      
      system.log.info(s"Job Finished: \r\n$slog ")
      dequeue(runner)
      
      if (inQueue.isEmpty) {
        sessionFinished()
      } else {
        tellAllInQueue(AnotherJobFinished(session, runner, result, slog))
      }

    case JobDeferred(runner, slog) =>
      dequeue(runner)
      system.log.info(s"Job Deferred: \r\n$slog")

      if (inQueue.isEmpty) {
        sessionFinished()
      } else {
        tellAllInQueue(AnotherJobHasBeenDeferred(runner))
      }

  }

  def tellAll(msg: Any):Unit = {
    runners.foreach(_.actorRef ! msg)
  }

  def tellAllInQueue(msg: Any):Unit = {
    inQueue.foreach(_.actorRef ! msg)
  }

  def dequeue(runner: JobRunnerRef):Unit ={
    inQueue = inQueue.filterNot(_ == runner)
  }

  def sessionFinished(): Unit = {
    inQueue = runners
    tellAll(SessionFinished(session))
    val endAt = DateTime.now
    val min = new Duration(sessionBeginAt, endAt).getStandardMinutes
    system.log.info(s"Sync session finish in $min minutes :  begin: $sessionBeginAt  end: $endAt id: ${session.id}")
    start()
  }
}
