package net.shantitree.flow.base.bznet.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.constant.MemberDataJournalTypeConst
import net.shantitree.flow.base.bznet.model.{AcpPosition, AcpPositionField, AcpPositionVW, BzNodeVW}
import net.shantitree.flow.base.biz.constant.EdgeLabelConst
import net.shantitree.flow.sys.lib.DML
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._

import scala.collection.JavaConverters._

object AcpPositionDML extends DML[AcpPosition]{
  val F = AcpPositionField
  val E = EdgeLabelConst

  def addNew(model: AcpPosition, doPositionUpdate: Boolean)(implicit g: OrientGraph): (AcpPositionVW, BzNodeVW) = {
    BzNetDML.findBzNode(model.member_code) match {
      case Some(vwBNode) =>
        addNewWithBNode(model, vwBNode, doPositionUpdate )
      case None =>
        throw new BzNodeNotFoundException(
          s"Can't add accomplished position data! BzNode code '${model.member_code}' is not found!")
    }
  }

  def addNewWithBNode(model: AcpPosition, vwBNode: BzNodeVW, doPositionUpdate: Boolean=true)(implicit g: OrientGraph): (AcpPositionVW, BzNodeVW) = {
    require(model.member_code == vwBNode.member_code)

    val vwAcpPosition = AcpPositionVW(g.addV(model))

    vwAcpPosition.v.addEdge(E.Accomplished, vwBNode.v)

    if (doPositionUpdate) {
      BzNetDML.updatePosition(vwBNode, vwAcpPosition.position)
    }

    (vwAcpPosition, vwBNode)
  }

  def addNew(memberCode: String, models: List[AcpPosition])(implicit g: OrientGraph): (BzNodeVW, List[AcpPositionVW]) = {

    BzNetDML.findBzNode(memberCode) match {
      case Some(vwBNode) =>
        if (models.nonEmpty) {
          val last = models.maxBy { m => (m.start_on, m.position) }
          if (vwBNode.position < last.position ||
            (vwBNode.position > last.position && last.promoted == MemberDataJournalTypeConst.Demoted)) {
            BzNetDML.updatePosition(vwBNode, last.position)
            vwBNode.save()
          }
        }

        vwBNode -> models.map { m =>
          require(m.member_code == memberCode)
          addNewWithBNode(m, vwBNode, doPositionUpdate = false)._1
        }

      case None =>
        throw new BzNodeNotFoundException(
          s"Can't add accomplished position data! " +
            s"BzNode code '$memberCode' is not found!")
    }
  }

  def findPositionOnDate(memberCode: String, position: Integer, startOn: Integer)(implicit g: OrientGraph): Option[AcpPositionVW] = {
    g.findV[AcpPosition](F.member_code -> memberCode, F.position -> position, F.start_on -> startOn).map(AcpPositionVW)
  }

  def lastAccomplished(memberCode: String)(implicit g: OrientGraph): Option[AcpPositionVW] = {

    val jIt = g.vertices[AcpPosition](F.member_code -> memberCode)

    if (jIt.iterator.hasNext)  {

      val it = jIt.asScala

      val v = if (it.size > 1) {
        it.maxBy { v => AcpPositionVW(v).start_on }
      } else {
        it.head
      }

      Some(AcpPositionVW(v))

    } else {
      None
    }

  }

}
