package net.shantitree.flow.dbsync.job

import com.tinkerpop.blueprints.impls.orient.{OrientGraph}

case class Updater(val update: OrientGraph => Any) extends TUpdater {

  /*
  def graphSessionTx[R](slog: SyncLog)(fn: OrientGraph => R): R = {
    if (slog.sync_type == SType.Bulk) {
      GraphSession.txWithMassInst(fn)
    } else {
      GraphSession.tx(fn)
    }
  }
  def graphSessionNoTx[R](slog: SyncLog)(fn: OrientGraphNoTx => R): R = {
    if (slog.sync_type == SType.Bulk) {
      GraphSession.noTxWithMassInst(fn)
    } else {
      GraphSession.noTx(fn)
    }
  }
  */

}
