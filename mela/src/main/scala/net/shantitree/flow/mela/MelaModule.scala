package net.shantitree.flow.mela

import akka.actor.Actor
import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.sys.event.BusinessEvent
import net.shantitree.flow.sys.module.SysModule

class MelaModule extends AbstractModule with ScalaModule with SysModule {

  override def configure() = {
    bind[Actor].annotatedWithName(BusinessEvent.actorName).to[BusinessEvent]
    bind[Actor].annotatedWithName(MelaController.actorName).to[MelaController]
  }

}
