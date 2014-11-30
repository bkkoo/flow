package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.BaseQry
import net.shantitree.flow.dbsync.job.Job
import net.shantitree.flow.dbsync.model.SyncLog

case class PulledDatum[M <: TModel](datum: List[Product], slog: SyncLog)
