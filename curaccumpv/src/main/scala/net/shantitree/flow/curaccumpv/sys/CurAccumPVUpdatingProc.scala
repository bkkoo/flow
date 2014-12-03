package net.shantitree.flow.curaccumpv.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.sys.GraphSession
import net.shantitree.flow.sys.lib.biz.PV
import net.shantitree.flow.base.biz.constant.EdgeLabelConst
import net.shantitree.flow.curaccumpv.model.{CurAccumPVLog, CurAccumPVLogVW, CurAccumPVLogStatus}
import net.shantitree.flow.base.saleorder.sys.SaleOrderBL
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession
import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap

import BzNodeExt._

import scala.util.Try

object CurAccumPVUpdatingProcImpl extends CurAccumPVUpdatingProc {
  
  val session = GraphSession
  import session._
  
  def process(comPeriod: Int) = {
    val saleOrders = tx { implicit g =>
      val lastCode = findLastProcessedSaleOrderCode(comPeriod)
      qryUnprocessedSaleOrders(comPeriod, lastCode)
    }
    val preLog = logPreProcessing(comPeriod, saleOrders)
    tx { implicit g =>
      val receivers = genReceiversOfBenefits(saleOrders)
      updateAccumPV(comPeriod, receivers, preLog)
    }
  }
}

trait CurAccumPVUpdatingProc {
  
  val session:TGraphSession

  import session._

  type BzNodeGetter = ()=>Option[BzNodeVW]
  type ReceiversBuffer = TrieMap[String, (BzNodeGetter, PV) ]

  val E = EdgeLabelConst

  def updateAccumPV(comPeriod: Int, receivers: Option[ReceiversBuffer], log: Option[CurAccumPVLogVW])(implicit g: OrientGraph): Option[Try[Vector[(BzNodeVW, PV)]]] = {
    
    for { 
      buffer <- receivers 
      preLog <- log
      
    } yield {
      Try {
        preLog.setToSuccess()
        for {
          (code, (getBzNode, pv)) <- buffer.toVector
          optBzNode = getBzNode() if optBzNode.nonEmpty
          bzNode = optBzNode.get
        } yield {
          bzNode
            .curAccumPV(comPeriod)
            .map(_.setPV(pv))
            .getOrElse(CurAccumPVDML.addNew(bzNode, comPeriod, pv))
          (bzNode, pv)
        }
      }
    }
  }

  def genReceiversOfBenefits(saleOrders: Vector[SaleOrderHeaderVW])(implicit g: OrientGraph): Option[ReceiversBuffer] = {
    if (saleOrders.nonEmpty) {
      for { benefits <- aggregateBuyerBenefits(saleOrders) } yield {
        aggregateReceiversOfBenefits(benefits)
      }
    } else {
      None
    }
  }

  def logPreProcessing(comPeriod:Integer, saleOrders: Vector[SaleOrderHeaderVW]) : Option[CurAccumPVLogVW] = {
    
    if (saleOrders.nonEmpty) {
      val preLog = tx { implicit g1 =>
        val fromCode = saleOrders.minBy(_.code).code
        val toCode = saleOrders.maxBy(_.code).code
        val log = CurAccumPVLog( com_period = comPeriod ,from_code = fromCode ,to_code = toCode, status = CurAccumPVLogStatus.Processing )
        LoggingUtil.recordLog(log)(g1)
      }

      Some(preLog)
      
    } else {
      None
    }
  }

  def qryUnprocessedSaleOrders(comPeriod: Integer, lastCode: Option[String])(implicit g: OrientGraph): Vector[SaleOrderHeaderVW] = {
    val itr = SaleOrderBL.qryOnComPeriod(comPeriod, lastCode).scalaIterator()
    if (itr.hasNext) { itr.toVector } else { Vector() }
  }
  
  def findLastProcessedSaleOrderCode(comPeriod: Integer)(implicit g: OrientGraph):Option[String] = {
    for { log <- LoggingUtil.findLastLog(comPeriod) } yield { log.to_code }
  }

  @tailrec
  final protected def aggregateAccumPV(buffer: ReceiversBuffer, code: String, value: (BzNodeGetter, PV) ): Unit = {
    val success = buffer.putIfAbsent(code, value) match {
      case Some(old@(fn, oldPV)) =>
        /* ensuring that no other concurrent threads has already replaced
         value of this key by checking against the old value! */
        buffer.replace(code, old, (fn, oldPV ++ value._2))
      case None =>
        true
    }
    // retry if not success!
    if (!success) { aggregateAccumPV(buffer, code, value) }
  }

  protected def aggregateReceiversOfBenefits(buyerBenefitsBuffer: ReceiversBuffer)(implicit g: OrientGraph): ReceiversBuffer = {
    /* receiver of benefits: the buyer itself (downline) + all of his uplines in chain */
    val buffer: ReceiversBuffer = TrieMap()
    tx { implicit g =>
      for {
        (code, (getBzNode, pv)) <- buyerBenefitsBuffer.toVector
        optVDownline = getBzNode() if optVDownline.nonEmpty
        vwBzNode <- optVDownline.get.chain()
      } {
        val getBzNode2 = () => Some(vwBzNode)
        aggregateAccumPV(buffer, vwBzNode.member_code, (getBzNode2, pv))
      }
    }
    buffer
  }

  protected def aggregateBuyerBenefits(saleOrders: Vector[SaleOrderHeaderVW])(implicit g: OrientGraph): Option[ReceiversBuffer] = {
    /* buyer benefits: the buyer it self (downline)  */
    if (saleOrders.nonEmpty) {
      val buffer:ReceiversBuffer = TrieMap()
      tx { implicit g =>
        for { saleOrder <- saleOrders } {
          val getBzNode = ()=> saleOrder.bzNode()
          aggregateAccumPV(buffer, saleOrder.member_code, ( getBzNode, saleOrder.pv() ))
        }
      }
      Some(buffer)
    } else {
      None
    }
  }
}
