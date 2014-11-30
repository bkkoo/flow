package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.result.TSyncResult
import net.shantitree.flow.dbsync.session.JobRunnerRef

case class JobFinished(jobRunner:JobRunnerRef, result: TSyncResult, slog: SyncLog)
