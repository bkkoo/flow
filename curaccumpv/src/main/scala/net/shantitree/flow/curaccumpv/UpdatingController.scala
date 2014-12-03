package net.shantitree.flow.curaccumpv

import akka.actor._
import com.google.inject.Inject
import com.google.inject.name.Named
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.base.saleorder.sys.SaleOrderBL
import net.shantitree.flow.curaccumpv.sys.msg._
import net.shantitree.flow.curaccumpv.sys.{CurAccumPVDML, CurAccumPVProc, LoggingUtil}
import net.shantitree.flow.sys.biz.ComPeriodUtil
import net.shantitree.flow.sys.event.BusinessEventPayload.RidOfNewSales
import net.shantitree.flow.sys.lib.eventbus.SimpleBus.{Publish, Unsubscribe, Subscribe}
import net.shantitree.flow.sys.module.NamedActor
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import net.shantitree.flow.sys.event.BusinessEventTopic.{NewSales, UpdateCurAccumPV}
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._

object UpdatingController extends NamedActor {

  private lazy val aggregatorActorName = "Aggregator"
  private lazy val updaterActorName = "Updater"

  val actorName = "UpdatingController"

  private class Aggregator (val session: TGraphSession, val updater: ActorRef) extends Actor {

    import context._
    import net.shantitree.flow.base.biz.PVCalc._

    def receive = {
      case AggregateReceivers(comPeriod, saleOrders, log, regenerateAllFlag) =>
        for { receivers <- adaptiveAggregatePVofReceiver(saleOrders, session) } {
          updater ! UpdateAccumPV(comPeriod, receivers, log, regenerateAllFlag)
        }
    }

  }

  private class Updater (val businessEvent: ActorSelection, val session: TGraphSession) extends Actor {

    import session._

    def receive = {
      case UpdateAccumPV(comPeriod, receivers, log, regenerateAllFlag)  =>
        val result = tx { implicit g =>
          CurAccumPVProc.updateAccumPV(comPeriod, receivers, log, regenerateAllFlag)
        }
        if (result.nonEmpty) {
          businessEvent ! Publish(UpdateCurAccumPV, result)
        }

      case DeleteAll(comPeriod) =>
        tx { implicit g =>
          CurAccumPVDML.deleteAllOnComPeriod(comPeriod)
        }
    }
  }

}

class UpdatingController @Inject() (@Named("BusinessEventActorPath") val businessEventActorPath: String, val session: TGraphSession, val comPeriodUtil: ComPeriodUtil) extends Actor with ActorLogging {

  import context._
  import UpdatingController._
  import session._

  val util = CurAccumPVProc

  val businessEvent = actorSelection(businessEventActorPath)

  lazy val updater = actorOf(Props(classOf[Updater], businessEvent , session), name = updaterActorName)
  lazy val aggregator = actorOf(Props(classOf[Aggregator], session, updater), name = aggregatorActorName)

  def receive = {

    case StartMon =>
      businessEvent ! Subscribe(NewSales, self)

    case StopMon =>
      businessEvent ! Unsubscribe(self, NewSales)

    case RidOfNewSales(rids) =>

      val groupOfSaleOrders = tx { implicit g =>
        rids.map { rid =>
          g.getV(SaleOrderHeaderVW)(rid)
        }.filter(_.nonEmpty)
        .map(_.get)
        .toVector
        .groupBy(_.com_period)
      }

      for { (comPeriod, saleOrders) <- groupOfSaleOrders } {
        process(comPeriod)(saleOrders)
      }

    case InitialCheckForNewSales =>

      val pending = tx { implicit g => util.qryPendingProcessSales() }

      for { (comPeriod, saleOrders) <- pending } {
        process(comPeriod)(saleOrders.toVector)
      }

      val curPeriod = comPeriodUtil.current
      process(curPeriod) { tx { implicit g => util.qryUnprocessedSales(curPeriod) }}

      for { overlappedPeriod <- comPeriodUtil.currentOverlappedComPeriod } {
        process(overlappedPeriod) { tx { implicit g =>  util.qryUnprocessedSales(overlappedPeriod) }}
      }

    case ReGenerateAll(optComPeriod) =>
      val comPeriod = optComPeriod.getOrElse(comPeriodUtil.current)
      process(comPeriod, regenerateAllFlag = true){ tx { implicit g => SaleOrderBL.qryOnComPeriod(comPeriod).scalaIterator().toVector } }

    case m@DeleteAll(comPeriod) =>
      updater ! m

  }

  def process(comPeriod: Int, regenerateAllFlag: Boolean=false)(saleOrders: Vector[SaleOrderHeaderVW]): Unit = {
    if (saleOrders.nonEmpty) {
      val log = tx { implicit g => LoggingUtil.recordProcess(comPeriod, saleOrders) }
      aggregator ! AggregateReceivers(comPeriod, saleOrders, log.get)
    }
  }

}
