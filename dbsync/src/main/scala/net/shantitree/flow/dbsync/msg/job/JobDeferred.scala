package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.session.JobRunnerRef

case class JobDeferred(runner: JobRunnerRef, slog: SyncLog)
