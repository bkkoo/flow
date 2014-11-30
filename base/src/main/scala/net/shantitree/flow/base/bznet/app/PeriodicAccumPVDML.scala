package net.shantitree.flow.base.bznet.app

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model._
import net.shantitree.flow.biz.lib.DML
import net.shantitree.flow.sys.constant.EdgeLabelConst
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._

object PeriodicAccumPVDML extends DML[PeriodicAccumulatedPV] {

  val F = PeriodicAccumulatedPVField
  val E = EdgeLabelConst

  def addNew(accumulatedPV: AccumPV)(implicit g:OrientGraph): AccumPVVW = {
    val m  = accumulatedPV
    val record = findPeriodicAccumulatedPV(m.member_code, m.year).getOrElse {
      createNewRecord(m.member_code, m.year)._1
    }
    addNew(m, record)
  }

  def addNew(accumulatedPV: AccumPV, record: PeriodicAccumulatedPVVW)(implicit g:OrientGraph): AccumPVVW = {
    val m  = accumulatedPV
    val vw = AccumPVVW(record, m.month)
    if (record.get[Map[String, Long]](vw.period) == null) { vw.setPV(m.ppv, m.qpv, 0) }
    vw
  }

  def addNew(memberCode: String, year: Int, accumulatedPVs: Iterable[AccumPV])(implicit g:OrientGraph): Iterable[AccumPVVW] = {
    val record = findPeriodicAccumulatedPV(memberCode, year).getOrElse {
      createNewRecord(memberCode, year)._1
    }
    accumulatedPVs.map { m => addNew(m, record) }
  }

  def findPeriodicAccumulatedPV(memberCode: String, year: Integer)(implicit g:OrientGraph): Option[PeriodicAccumulatedPVVW] = {
    g.findV(PeriodicAccumulatedPVVW)(F.member_code -> memberCode, F.year -> year)
  }

  def createNewRecord(memberCode: String, year: Int)(implicit g:OrientGraph): (PeriodicAccumulatedPVVW, BzNodeVW) = {
    val vwBzNode = BzNetDML.findBzNode(memberCode).getOrElse {
      throw new BzNodeNotFoundException(
        s"Can't not create a 'PeriodicAccumulatedPV' record for BzNode '$memberCode'!")
    }
    val vw = g.addV(PeriodicAccumulatedPVVW)
    vw.setProperties(F.year -> year, F.member_code -> memberCode)
    vwBzNode.v.addEdge(E.PeriodicAcmPV, vw.v)
    (vw, vwBzNode)
  }


}
