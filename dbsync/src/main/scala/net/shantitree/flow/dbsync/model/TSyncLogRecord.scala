package net.shantitree.flow.dbsync.model

/**
 * Created by bkkoo on 20/11/2557.
 */
trait TSyncLogRecord {
  def next(): TSyncLogRecord
  def get: SyncLog
  def span(): TSyncLogRecord
}
