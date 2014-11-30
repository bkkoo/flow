package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.job.TUpdater
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.session.JobRunnerRef

case class RunUpdateInGraphSession(runner: JobRunnerRef, updater: TUpdater, slog: SyncLog)
