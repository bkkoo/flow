package net.shantitree.flow.dbsync

import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.google.inject.Guice
import net.shantitree.flow.dbsync.DbSyncModule
import net.shantitree.flow.sys.lib.actor.GuiceAkkaKernelUtil
import net.shantitree.flow.sys.lib.msg.Start
import net.codingwell.scalaguice.InjectorExtensions._
import net.shantitree.flow.sys.module.AkkaModule

trait DbSyncKernel extends Bootable with GuiceAkkaKernelUtil {

  val syncModule:DbSyncModule

  lazy val injector = Guice.createInjector( new AkkaModule(), syncModule )
  lazy val system:ActorSystem = injector.instance[ActorSystem]

  def startup() = {
    createActor(DbSyncController) ! Start
  }

  def shutdown() = {
    system.shutdown()
  }

}
