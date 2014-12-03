package net.shantitree.flow.sys.module

import com.google.inject.Provides
import com.google.inject.name.Named
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.sys.event.BusinessEvent
import net.shantitree.flow.sys.lib.actor.ActorPathConfig

trait SysModule { this: ScalaModule =>

  @Provides @Named("BusinessEventActorPath")
  def businessEventActorPathProvider(): String = {
    ActorPathConfig.actorPathOf(BusinessEvent)
  }

}
