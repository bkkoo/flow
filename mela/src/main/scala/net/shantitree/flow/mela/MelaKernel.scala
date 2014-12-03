package net.shantitree.flow.mela

import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.google.inject.Guice
import net.codingwell.scalaguice.InjectorExtensions._
import net.shantitree.flow.curaccumpv.{CurAccumPVController, CurAccumPVModule}
import net.shantitree.flow.mela.MelaModule
import net.shantitree.flow.sys.lib.actor.GuiceAkkaKernelUtil
import net.shantitree.flow.sys.module.AkkaModule

class MelaKernel extends Bootable with GuiceAkkaKernelUtil {

  lazy val injector = Guice.createInjector( new AkkaModule(), new MelaModule(), new CurAccumPVModule() )
  lazy val system = injector.instance[ActorSystem]

  def startup() = {
    createActor(MelaController)
    createActor(CurAccumPVController)
  }

  def shutdown() = {
    system.shutdown()
  }

}
