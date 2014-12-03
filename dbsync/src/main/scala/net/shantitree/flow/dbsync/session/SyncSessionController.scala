package net.shantitree.flow.dbsync.session

import akka.actor.{ActorRef, ActorLogging, Actor}
import com.typesafe.config.ConfigFactory
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.lib.msg.Start
import net.shantitree.flow.sys.module.NamedActor
import org.joda.time.{Duration, DateTime}
import scala.collection.JavaConverters._

object SyncSessionController extends NamedActor {

  val actorName = "SyncSessionController"

  lazy val runnerNames:Set[String] = {
    try {
      val config = ConfigFactory.load().getConfig("sync")
      config.getStringList("run_jobs").asScala.toSet
    } catch {
      case e:Exception =>
        Set()
    }
  }

}

class SyncSessionController extends Actor with ActorLogging {

  import context._

  lazy val runners: Set[JobRunnerRef] = {
    SyncSessionController.runnerNames.map { n =>
      val actorRef:ActorRef = try {
        actorOf(Gak(system).props(n), name = n)
      } catch {
        case e:Exception =>
          throw new RuntimeException(s"Exception ${e.getMessage} has occurred during create Job Runner! " +
            s"Please re-check 'job runner' name '$n' that it is the same as class name!")
      }
      JobRunnerRef(n, actorRef)
    }
  }

  var session:SyncSession = null
  var inQueue:Set[JobRunnerRef] = null
  private var sessionBeginAt: DateTime = null

  def start():Unit = {
    if (runners.nonEmpty) {
      session = SyncSession(runners, self)
      sessionBeginAt = DateTime.now
      inQueue = runners
      system.log.info(s"Sync session begin:  $sessionBeginAt  id: ${session.id}")
      runners.foreach { runner => runner.actorRef ! SessionBegin(session) }
    } else {
      log.warning("No job to run! please check 'sync.run_jobs' configuration!")
    }
  }

  def receive: Receive = {

    case Start =>
      start()

    case Stop =>
      //todo: Implement stop mechanism here!

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
