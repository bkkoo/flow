package net.shantitree.flow.curaccumpv.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.sys.BzNetDML
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.sys.lib.DML
import net.shantitree.flow.sys.lib.biz.PV
import net.shantitree.flow.base.biz.constant.EdgeLabelConst
import net.shantitree.flow.curaccumpv.model.{CurAccumPVVW, CurAccumPVField, CurAccumPV}
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.orient.graph.VertexHelper._
import net.shantitree.flow.sys.lib.orient.oql.operator.Relational._

object CurAccumPVDML extends DML[CurAccumPV] {

  val F = CurAccumPVField
  val E = EdgeLabelConst

  def get(code: String, comPeriod: Integer)(implicit g: OrientGraph): CurAccumPV = {
    g.findV[CurAccumPV](F.member_code -> code, F.com_period -> comPeriod)
      .map(_.toModel)
      .getOrElse(newModel(code, comPeriod))
  }

  def getOrCreate(code: String, comPeriod:Integer)(implicit g: OrientGraph): CurAccumPVVW = {
    g.findV(CurAccumPVVW)(F.member_code -> code, F.com_period -> comPeriod)
      .getOrElse { g.addV(CurAccumPVVW, newModel(code, comPeriod)) }
  }

  def newModel(code: String, comPeriod:Integer, pv: PV=PV())(implicit g: OrientGraph): CurAccumPV = {
    CurAccumPV(None, comPeriod, code, ppv=pv.ppv, qpv=pv.qpv, cpv=pv.cpv)
  }

  def addNew(code: String, comPeriod:Integer, pv: PV)(implicit g: OrientGraph): CurAccumPVVW = {
    val vwBzNode = BzNetDML.getBzNode(code)
    addNew(vwBzNode, comPeriod, pv)
  }

  def addNew(vwBzNode: BzNodeVW, comPeriod:Integer, pv: PV)(implicit g: OrientGraph): CurAccumPVVW = {
    val vw = g.addV(CurAccumPVVW, newModel(vwBzNode.member_code, comPeriod, pv))
    vw.v.addEdge(E.CurAccumPV, vwBzNode.v)
    vw
  }

  def get(vw: BzNodeVW, comPeriod: Integer)(implicit g: OrientGraph): Option[CurAccumPVVW] = {
    vw.v.in1(CurAccumPVVW)(E.CurAccumPV)
  }

  private [curaccumpv] def deleteAllOnComPeriod(comPeriod: Int)(implicit g: OrientGraph): Integer = {
    g.delete[CurAccumPV](F.com_period === comPeriod)
  }

}
