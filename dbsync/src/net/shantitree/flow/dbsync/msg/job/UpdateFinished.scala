package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.result.TSyncResult

case class UpdateFinished(result: TSyncResult, slog: SyncLog)
