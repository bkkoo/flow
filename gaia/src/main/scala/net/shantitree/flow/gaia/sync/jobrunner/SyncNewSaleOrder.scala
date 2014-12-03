package net.shantitree.flow.gaia.sync.jobrunner

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.saleorder.sys.SaleOrderDML
import net.shantitree.flow.gaia.sync.job.NewSaleOrder
import net.shantitree.flow.dbsync.job.JobRunner
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.dbsync.model.SyncLogUtil._
import net.shantitree.flow.sys.lib.{DataChangeTopic, DataChangeMethod, DataChangeEventBus}
import net.shantitree.flow.sys.module.NamedActor

object SyncNewSaleOrder extends NamedActor {
  val actorName = "SyncNewSaleOrder"
}

class SyncNewSaleOrder extends JobRunner(NewSaleOrder) {

  lazy val myBeginSync = rcvBeginSyncWithWaitForRunner("SyncNewMember")
  lazy val dataChangeTopic = DataChangeTopic("SaleOrder", DataChangeMethod.Create)

  def periodical:Receive = partialRcv {
    case UpdateFinished(result, slog) =>
      updateFinished(result, slog)
      /*
      val msg = DataChangeMsg(dataChangeTopic, result)
      eventBus.publish(msg)
      */

  } orElse myBeginSync

  def bulk:Receive = partialRcv {
    case PulledDatum(datum,slog) =>
      update(datum, slog)
      if (!pullNext()) {
        execInGraphSession(slog){ implicit g => finalizeOrphan() }
      }

  } orElse myBeginSync

  def finalizeOrphan()(implicit g:OrientGraph): Unit = {
    SaleOrderDML.finalizeOrphanBenefits()
    SaleOrderDML.finalizeOrphanBuyers()
  }

  def getPuller(slog: SyncLog) = job.createPuller(slog.toCreatedDuration)
}
