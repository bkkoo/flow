package net.shantitree.flow.base.bznet.model

import java.util.Date

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.app.BzNetBL
import net.shantitree.flow.base.bznet.constant.MemberPositionConst
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper}

object BzNodeVW extends VWrap[BzNode, BzNodeVW]{
  def apply(v: Vertex) = new BzNodeVW(v)
}

class BzNodeVW(val v: Vertex) extends TVertexWrapper[BzNode] {

  val f = BzNodeField

  def created_at = getDate(f.created_at)
  def set_created_at(value: Date) = {
    setProperties(f.created_at -> value, f.created_on -> value.toDateOnlyInt, f.created_ym -> value.toYearMonthInt)
  }

  def created_ym = getInt(f.created_ym)
  def created_on = getInt(f.created_on)

  def sponsor_code = getString(f.sponsor_code)
  def set_sponsor_code(value: String) = setString(f.sponsor_code, value)

  def member_code = getString(f.member_code)
  def set_member_code(value: String) = setString(f.member_code, value)

  def position = getInt(f.position)
  def set_position(value: Integer) = {
    if (MemberPositionConst.isPosition(value)) {
      setInt(f.position, value)
    } else {
      throw new RuntimeException(s"Can't set BzNode's position with invalid position value '$value'")
    }
  }

  def uplines()(implicit g: OrientGraph): Iterator[BzNodeVW] = BzNetBL.getUplines(this)
  def chain()(implicit g: OrientGraph): Iterator[BzNodeVW] = BzNetBL.getChain(this)
  def vUplines()(implicit g: OrientGraph): Iterator[Vertex] = BzNetBL.getUplines(v)
  def vChain()(implicit g: OrientGraph): Iterator[Vertex] = BzNetBL.getChain(v)

}
