package net.shantitree.flow.sys.lib.module

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Inject, Injector, Provider}
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.lib.module.AkkaModule.ActorSystemProvider

object AkkaModule {

  lazy val config = ConfigFactory.load()

  class ActorSystemProvider @Inject() (val injector: Injector) extends Provider[ActorSystem] {
    override def get() = {
      val system = ActorSystem("flow", config)
      Gak(system).initialize(injector)
      system
    }
  }

}

/** A module providing an Akka ActorSystem. */
class AkkaModule extends AbstractModule with ScalaModule {

  override def configure() {
    bind[ActorSystem].toProvider[ActorSystemProvider].asEagerSingleton()
  }
}
