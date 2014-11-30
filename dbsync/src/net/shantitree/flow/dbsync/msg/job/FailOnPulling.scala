package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.model.SyncLog

case class FailOnPulling(e: Throwable, slog: SyncLog)
