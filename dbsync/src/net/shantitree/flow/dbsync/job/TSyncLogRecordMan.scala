package net.shantitree.flow.dbsync.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.BaseQry
import net.shantitree.flow.dbsync.model.{TSyncLogRecord, SyncLog, SyncLogRecord}

trait TSyncLogRecordMan { this: JobRunner[_<:BaseQry, _<:TModel] =>
  private var syncLogRecord: TSyncLogRecord = null

  def getSyncLogRecord() = {
    if (syncLogRecord == null) { syncLogRecord = SyncLogRecord.create(runnerRef, config) }
    syncLogRecord
  }
  def nextSyncLogRecord(slog: SyncLog) = {
    syncLogRecord = SyncLogRecord(config, slog).next()
    syncLogRecord
  }
  def nextSyncLogRecord() = {
    syncLogRecord = syncLogRecord.next()
    syncLogRecord
  }
  def spanSyncLogRecord() = {
    syncLogRecord = syncLogRecord.span()
    syncLogRecord
  }

}
