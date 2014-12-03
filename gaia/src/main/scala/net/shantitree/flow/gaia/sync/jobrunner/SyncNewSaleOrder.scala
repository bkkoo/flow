package net.shantitree.flow.gaia.sync.jobrunner

import com.google.inject.Inject
import com.google.inject.name.Named
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.saleorder.sys.SaleOrderDML
import net.shantitree.flow.gaia.sync.job.NewSaleOrder
import net.shantitree.flow.dbsync.job.JobRunner
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.job._
import net.shantitree.flow.dbsync.model.SyncLogUtil._
import net.shantitree.flow.sys.lib.eventbus.SimpleBus.Publish
import net.shantitree.flow.sys.module.NamedActor
import net.shantitree.flow.sys.event.BusinessEventTopic.NewSales
import net.shantitree.flow.sys.event.BusinessEventPayload.RidOfNewSales

object SyncNewSaleOrder extends NamedActor {
  val actorName = "SyncNewSaleOrder"
}

class SyncNewSaleOrder @Inject() (@Named("BusinessEventActorPath") val businessEventActorPath: String) extends JobRunner(NewSaleOrder) {

  lazy val myBeginSync = rcvBeginSyncWithWaitForRunner("SyncNewMember")
  lazy val businessEvent = context.actorSelection(businessEventActorPath)

  def periodical:Receive = partialRcv {
    case UpdateFinished(result, slog) =>
      updateFinished(result, slog)
      val payload = result.asInstanceOf[List[AnyRef]]
      businessEvent ! Publish(NewSales, RidOfNewSales(payload))

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
