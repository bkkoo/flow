package net.shantitree.flow.base.bznet.model

import java.util.Date

import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper}

object AcpPositionVW extends VWrap[AcpPosition, AcpPositionVW]{
  def apply(v: Vertex) = new AcpPositionVW(v)
}

class AcpPositionVW(val v: Vertex) extends TVertexWrapper[AcpPosition] {

  def start_at = getDate("start_at")
  def set_start_at(value: Date) = {
    setProperties("start_at" -> value, "start_on" -> value.toDateOnlyInt)
  }

  def start_on = getInt("start_on")

  def member_code = getString("member_code")
  def set_member_code(value: String) = setString("member_code", value)

  def promoted = getInt("promoted")
  def set_promoted(value: Integer) = setInt("promoted", value)

  def position = getInt("position")
  def set_position(value: Integer) = setInt("position", value)

  def created_at = getDate("created_at")
  def set_created_at(value: Date) = setDate("created_at", value)

}

