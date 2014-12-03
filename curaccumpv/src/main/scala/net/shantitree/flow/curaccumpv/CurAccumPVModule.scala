package net.shantitree.flow.curaccumpv

import akka.actor.Actor
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

class CurAccumPVModule extends AbstractModule with ScalaModule {
  override def configure() = {
    bind[Actor].annotatedWithName(UpdatingController.actorName).to[UpdatingController]
  }
}
