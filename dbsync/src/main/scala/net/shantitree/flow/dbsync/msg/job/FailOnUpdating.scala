package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.job.TUpdater
import net.shantitree.flow.dbsync.model.SyncLog

case class FailOnUpdating(e: Throwable, updater: TUpdater, slog: SyncLog)
