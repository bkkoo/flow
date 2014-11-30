package net.shantitree.flow.base.biz

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderVW
import net.shantitree.flow.sys.lib.biz.PV
import net.shantitree.flow.sys.lib.orient.graph.TGraphSession

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}
import PVCalc.{ConCurrentBuffer, NonConCurrentBuffer}

object PVCalcChainAggregator {

  implicit object ConCurrentChainAggregator extends PVCalcChainAggregator[ConCurrentBuffer]  {

    @tailrec
    def aggregate(memberCode:String, bzNode: BzNodeVW, pv: PV, buffer: ConCurrentBuffer):Unit  = {
      val success = buffer.putIfAbsent(memberCode, (()=>bzNode, pv)) match {
        case Some(old@(fn, oldPV)) =>
          /* ensuring that no other concurrent threads has already replaced
           value of this key by checking against the old value! */
          buffer.replace(memberCode, old, (fn, oldPV ++ pv))
        case None =>
          true
      }
      // retry if not success!
      if (!success) { aggregate(memberCode, bzNode, pv, buffer) }
    }
  }

  implicit object SingleThreadChainAggregator extends PVCalcChainAggregator[NonConCurrentBuffer]  {
    def aggregate(memberCode:String, bzNode: BzNodeVW, pv: PV, buffer: NonConCurrentBuffer):Unit  = {
      val sumPV = buffer.get(memberCode).map { case (_, oldPV) =>
        oldPV ++ pv
      }.getOrElse {
        pv
      }
      buffer.put(memberCode, (bzNode, sumPV))
    }
  }
}

trait PVCalcChainAggregator[T] {
  def aggregate(memberCode:String, bzNode: BzNodeVW, pv: PV, buffer: T):Unit
}

object PVCalc {
  import scala.collection.mutable
  // Because 'BzNodeVW' is mutable, it need to be wrap with function to provide
  // function object as a stable (immutable) value part for 'ConCurrentBuffer'
  type BzNodeGetter = ()=>BzNodeVW
  type ConCurrentBuffer = TrieMap[String, (BzNodeGetter, PV) ]
  type NonConCurrentBuffer = mutable.Map[String, (BzNodeVW, PV)]

  def aggregatePVofBuyer(saleOrders: TraversableOnce[SaleOrderHeaderVW])(implicit g: OrientGraph): TraversableOnce[(String, BzNodeVW, PV)] = {
    for {
      saleOrder <- saleOrders
      bzNode <- saleOrder.bzNode()
    } yield {
      (saleOrder.member_code, bzNode, saleOrder.pv())
    }
  }

  def aggregatePVofReceiver[B](buffer:B, saleOrders: TraversableOnce[SaleOrderHeaderVW])(implicit g: OrientGraph, collector: PVCalcChainAggregator[B]): Option[B] = {
    if (saleOrders.nonEmpty) {
      val pvOfBuyers = aggregatePVofBuyer(saleOrders)
      for {
        (code, buyerBzNode, pv) <- pvOfBuyers
        bzNode <- buyerBzNode.chain()
      } {
        collector.aggregate(bzNode.member_code, bzNode, pv, buffer)
      }
      Some(buffer)
    } else {
      None
    }
  }
  
  def adaptiveAggregatePVofReceiver(saleOrders: Iterable[SaleOrderHeaderVW], session: TGraphSession, threads: Int=4, splitAtSize:Int=50)(implicit ec: ExecutionContext): Future[Map[String, (BzNodeVW, PV)]] = {
    
    val size = saleOrders.size
    
    if (size <= splitAtSize) {
      
      // little records ... process in single thread and using non concurrent buffer (mutable Map)
      val buffer: NonConCurrentBuffer = mutable.Map()
      
      for {_ <- Future {
        session.tx { implicit g => aggregatePVofReceiver(buffer, saleOrders) }
        
      }} yield {
        buffer.toMap
      }
      
    } else {
      
      // split processing in multi threads and using concurrent buffer (TrieMap)
      val buffer: ConCurrentBuffer = TrieMap()
      val f1 = Future { saleOrders.grouped(threads) }
      
      f1.flatMap { groups =>

        val fs = groups.map { gSaleOrders => Future {
          session.tx { implicit g => aggregatePVofReceiver(buffer, gSaleOrders) }
        }}

        Future.sequence(fs)
        
      }.map { u => buffer.mapValues { case (getBzNode, pv) => (getBzNode(), pv)}.toMap }
      
    }
  }
  
}
