package net.shantitree.flow.gaia.sync.module

import akka.actor.Actor
import com.google.inject._
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.gaia.sync.jobrunner.{SyncNewPeriodicAcmPV, SyncNewAcpPosition, SyncNewSaleOrder, SyncNewMember}
import net.shantitree.flow.dbsync.module.TSyncModule
import net.shantitree.flow.sys.GraphSession
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession

class SyncModule
  extends AbstractModule
  with ScalaModule
  with TSyncModule {

  override def configure() {

    bind[Actor].annotatedWithName(SyncNewMember.actorName).to[SyncNewMember]
    bind[Actor].annotatedWithName(SyncNewSaleOrder.actorName).to[SyncNewSaleOrder]
    bind[Actor].annotatedWithName(SyncNewAcpPosition.actorName).to[SyncNewAcpPosition]
    bind[Actor].annotatedWithName(SyncNewPeriodicAcmPV.actorName).to[SyncNewPeriodicAcmPV]
    bind[TGraphSession].toInstance(GraphSession)

    syncModuleConfigure()

  }


}
