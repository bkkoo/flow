package net.shantitree.flow.curaccumpv

import akka.actor.Actor
import net.shantitree.flow.sys.lib.actor.GuiceActorUtil
import net.shantitree.flow.sys.module.NamedActor

object CurAccumPVController extends NamedActor {
  val actorName = "CurAccumPVController"
}

class CurAccumPVController extends Actor with GuiceActorUtil {

  val updatingController = createActor(UpdatingController)

  def receive = {
    case _ =>
  }

}
