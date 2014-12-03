package net.shantitree.flow.dbsync

import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.google.inject.Guice
import net.shantitree.flow.dbsync.module.TSyncModule
import net.shantitree.flow.sys.lib.guice.Gak
import net.shantitree.flow.sys.lib.module.AkkaModule
import net.shantitree.flow.sys.lib.msg.Start
import net.codingwell.scalaguice.InjectorExtensions._

trait DbSyncKernel extends Bootable {

  val syncModule:TSyncModule

  lazy val injector = Guice.createInjector( new AkkaModule(), syncModule )
  lazy val system:ActorSystem = injector.instance[ActorSystem]

  def startup() = {
    val appController = system.actorOf(Gak(system).props(AppController.actorName), name = AppController.actorName)
    appController ! Start
  }

  def shutdown() = {
    system.shutdown()
  }

}
