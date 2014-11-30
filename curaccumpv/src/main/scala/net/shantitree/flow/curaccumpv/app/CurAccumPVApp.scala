package net.shantitree.flow.curaccumpv.app

import akka.actor._
import net.shantitree.flow.curaccumpv.app.msg._
import net.shantitree.flow.base.saleorder.app.SaleOrderBL
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import net.shantitree.flow.sys._
import net.shantitree.flow.base.com.PVCalc

object CurAccumPVApp {

  private lazy val aggregatorActorName = "CurAccumPVAggregator"
  private lazy val updaterActorName = "CurAccumPVUpdater"
  private lazy val SaleOrderDomain = "SaleOrder"

  lazy val controllerName = "CurAccumPVAppController"
  lazy val DataName = "CurAccumPv"


  def props(bus: DataChangeEventBus, session: TGraphSession, comPeriodUtil: ComPeriodUtil):Props = {
    Props(new Controller(bus, session, comPeriodUtil))
  }

  def createController(context: ActorContext, bus: DataChangeEventBus, session: TGraphSession, comPeriodUtil: ComPeriodUtil): ActorRef = {
    context.actorOf(props(bus, session, comPeriodUtil), name= controllerName )
  }

  class Controller (val bus: DataChangeEventBus, val session: TGraphSession, val comPeriodUtil: ComPeriodUtil) extends Actor with ActorLogging {

    import session._
    import context._

    val util = CurAccumPVProc

    lazy val updater = actorOf(Props(classOf[Updater], bus, session), name = updaterActorName)
    lazy val aggregator = actorOf(Props(classOf[Aggregator], session, updater), name = aggregatorActorName)

    def receive = {

      case StartMon =>
        bus.subscribe(self, DataChangeTopic(SaleOrderDomain, DataChangeMethod.Create) )

      case StopMon =>
        bus.unsubscribe(self)

      case NewSales(codes) =>

        log.info(codes.mkString(","))

        val groupOfSaleOrders = tx { implicit g =>
          SaleOrderBL.getSaleOrders(codes).toVector.groupBy(_.com_period)
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

  private class Aggregator (val session: TGraphSession, val updater: ActorRef) extends Actor with CurAccumPVUpdatingProc {

    import PVCalc._
    import context._

    def receive = {
      case AggregateReceivers(comPeriod, saleOrders, log, regenerateAllFlag) =>
        for { receivers <- adaptiveAggregatePVofReceiver(saleOrders, session) } {
          updater ! UpdateAccumPV(comPeriod, receivers, log, regenerateAllFlag)
        }
    }

  }

  private class Updater (val bus: DataChangeEventBus, val session: TGraphSession) extends Actor with CurAccumPVUpdatingProc {

    import session._

    def receive = {
      case UpdateAccumPV(comPeriod, receivers, log, regenerateAllFlag)  =>
        val result = tx { implicit g =>
          CurAccumPVProc.updateAccumPV(comPeriod, receivers, log, regenerateAllFlag)
        }
        if (result.nonEmpty) {
          bus.publish(DataChangeMsg(DataChangeTopic("CurAccumPV", DataChangeMethod.Update), result))
        }

      case DeleteAll(comPeriod) =>
        val result = tx { implicit g =>
          CurAccumPVDML.deleteAllOnComPeriod(comPeriod)
        }

        if (result > 0) {
          bus.publish(DataChangeMsg(DataChangeTopic("CurAccumPV", DataChangeMethod.DeleteAll), result))
        }
    }
  }

}

