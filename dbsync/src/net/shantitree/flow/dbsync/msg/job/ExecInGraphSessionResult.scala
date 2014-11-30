package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.model.SyncLog

import scala.util.Try

case class ExecInGraphSessionResult(slog: SyncLog, result: Try[Any])
