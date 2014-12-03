package net.shantitree.flow.gaia.sync

import akka.actor.Actor
import com.google.inject._
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.dbsync.DbSyncModule
import net.shantitree.flow.gaia.sync.jobrunner.{SyncNewAcpPosition, SyncNewMember, SyncNewPeriodicAcmPV, SyncNewSaleOrder}
import net.shantitree.flow.sys.GraphSession
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import net.shantitree.flow.sys.module.SysModule

class SyncModule
  extends AbstractModule
  with ScalaModule
  with DbSyncModule
  with SysModule {

  override def configure() {

    bind[Actor].annotatedWithName(SyncNewMember.actorName).to[SyncNewMember]
    bind[Actor].annotatedWithName(SyncNewSaleOrder.actorName).to[SyncNewSaleOrder]
    bind[Actor].annotatedWithName(SyncNewAcpPosition.actorName).to[SyncNewAcpPosition]
    bind[Actor].annotatedWithName(SyncNewPeriodicAcmPV.actorName).to[SyncNewPeriodicAcmPV]
    bind[TGraphSession].toInstance(GraphSession)

    syncModuleConfigure()

  }


}
