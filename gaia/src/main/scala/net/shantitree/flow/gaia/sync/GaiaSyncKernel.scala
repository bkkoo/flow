package net.shantitree.flow.gaia.sync

import net.shantitree.flow.dbsync.DbSyncKernel
import net.shantitree.flow.gaia.sync.module.SyncModule

class GaiaSyncKernel extends DbSyncKernel {
  val syncModule = new SyncModule()
}
