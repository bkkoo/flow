package net.shantitree.flow.gaia.sync

import akka.actor.Actor
import net.shantitree.flow.dbsync.session.TSyncSessionCtrl

class SyncSessionCtrl extends Actor with TSyncSessionCtrl {
  val runners = createRunners("SyncNewMember", "SyncNewSaleOrder",  "SyncNewAcpPosition", "SyncNewPeriodicAcmPV")
}
