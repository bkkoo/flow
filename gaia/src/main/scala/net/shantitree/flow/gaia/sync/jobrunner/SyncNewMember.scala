package net.shantitree.flow.gaia.sync.jobrunner

import net.shantitree.flow.base.bznet.sys.BzNetDML
import net.shantitree.flow.gaia.sync.job.NewMember
import net.shantitree.flow.dbsync.job.JobRunner
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.dbsync.model.SyncLogUtil._
import net.shantitree.flow.sys.module.NamedActor

object SyncNewMember extends NamedActor {
  val actorName = "SyncNewMember"
}

class SyncNewMember
  extends JobRunner(NewMember) {

  def periodical:Receive = rcvBeginSyncWithDelayRunning

  def bulk:Receive = partialRcv {
    case PulledDatum(datum, slog) =>
      update(datum, slog)
      if (datum.nonEmpty) {
        execInGraphSession(slog){ implicit g => BzNetDML.sponsorAllOrphans() }
      }
      if (!pullNext()) {
        execInGraphSession(slog){ implicit g => BzNetDML.finalizeOrphans() }
      }

  } orElse rcvDefault

  def getPuller(slog: SyncLog) = job.createPuller(slog.toCreatedDuration)

}
