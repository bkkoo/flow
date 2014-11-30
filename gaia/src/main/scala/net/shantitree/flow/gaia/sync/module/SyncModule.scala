package net.shantitree.flow.gaia.sync.module

import akka.actor.Actor
import com.google.inject._
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.gaia.sync.SyncSessionCtrl
import net.shantitree.flow.gaia.sync.jobrunner.{SyncNewPeriodicAcmPV, SyncNewAcpPosition, SyncNewSaleOrder, SyncNewMember}
import net.shantitree.flow.slick.module.TSqlDbModule
import net.shantitree.flow.dbsync.module.TSyncModule

class SyncModule
  extends AbstractModule
  with ScalaModule
  with TSyncModule
  with TSqlDbModule {

  val syncJobConfigPath = "flow.gaia.sync-job"
  val sqlDbConfigPath = "flow.gaia.database"

  override def configure() {

    bind[Actor].annotatedWithName("SyncNewMember").to[SyncNewMember]
    bind[Actor].annotatedWithName("SyncNewSaleOrder").to[SyncNewSaleOrder]
    bind[Actor].annotatedWithName("SyncNewAcpPosition").to[SyncNewAcpPosition]
    bind[Actor].annotatedWithName("SyncNewPeriodicAcmPV").to[SyncNewPeriodicAcmPV]
    bind[Actor].annotatedWithName("SyncSessionCtrl").to[SyncSessionCtrl]

    syncModuleConfigure()
    sqlDbModuleConfigure()

  }


}
