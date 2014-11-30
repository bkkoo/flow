package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.param.Param
import net.shantitree.flow.slick.qry.{Condition, View, BaseQry}
import net.shantitree.flow.dbsync.job.Job
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.puller.TPuller

object PullRequest {
  def create[P <: Param, Q <: BaseQry, M <: TModel](job: Job[Q, M], param: P, slog: SyncLog)(implicit condition: Condition[P, Q])  = {
    val puller = job.createPuller(param)
    PullRequest(puller, slog)
  }
}

case class PullRequest[Q <: BaseQry](puller: TPuller[Q], slog: SyncLog)
