package net.shantitree.flow.dbsync.model

import net.shantitree.flow.dbsync.job.SyncJobConfig
import net.shantitree.flow.dbsync.session.JobRunnerRef
import net.shantitree.flow.sys.GraphSession
import org.joda.time.DateTime


object SyncLogRecord {
  def create(runnerRef: JobRunnerRef, config: SyncJobConfig) = {
    val slog = GraphSession.tx { implicit g => SyncLogUtil.getNewSyncDurationLog(config) }
    SyncLogRecord(config, slog)
  }
}

case class SyncLogRecord(config: SyncJobConfig , priorLog: SyncLog) extends TSyncLogRecord {

  def next() = {
    val nextLog = SyncLogUtil.getNewSyncDurationLog(priorLog, config)
    this.copy(priorLog = nextLog)
  }

  def span() = {
    val nextLog = priorLog.copy(until = Some(DateTime.now.toDate))
    this.copy(priorLog = nextLog)
  }

  def get = priorLog

}
