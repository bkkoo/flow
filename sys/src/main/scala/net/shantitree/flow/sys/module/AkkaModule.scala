package net.shantitree.flow.sys.module

import akka.actor.ActorSystem
import com.google.inject.{AbstractModule, Inject, Injector, Provider}
import com.typesafe.config.ConfigException.Missing
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.module.AkkaModule.ActorSystemProvider

object AkkaModule {

  lazy val config = ConfigFactory.load()
  lazy val systemName = try {
    config.getString("flow.actorSystemName")
  } catch {
    case e:Missing =>
      throw new Missing("Can't find setting for 'actorSystemName'")
    case e:Exception =>
      throw new RuntimeException(s"Can't get setting for 'actorSystemName' due to following error '${e.getMessage}'")
  }

  class ActorSystemProvider @Inject() (val injector: Injector) extends Provider[ActorSystem] {

    override def get() = {
      val system = ActorSystem(systemName, config)
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
