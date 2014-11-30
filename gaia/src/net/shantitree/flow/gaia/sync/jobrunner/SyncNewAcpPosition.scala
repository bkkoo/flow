package net.shantitree.flow.gaia.sync.jobrunner

import net.shantitree.flow.gaia.sync.job.NewAcpPosition
import net.shantitree.flow.dbsync.job.JobRunner
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.model.SyncLogUtil._

class SyncNewAcpPosition
  extends JobRunner(NewAcpPosition) {

  def periodical = rcvBeginSyncWithRunOnOverdue
  def bulk = rcvBeginSyncWithWaitForRunner("SyncNewMember")
  def getPuller(slog: SyncLog) = job.createPuller(slog.toCreatedDuration)

}
