package net.shantitree.flow.sys.lib

import java.util.Date

import net.shantitree.flow.sys.lib.model.{Model, TVertexWrapper}

trait TSysLogField {
  val created_at = "created_at"
  val modified_at = "modified_at"
}

trait TSysLogVW {this: TVertexWrapper[_<: Model] =>
  private val F = SysLogField
  def created_at = getDate(F.created_at)
  def set_created_at(value: Date) = setDate(F.created_at, value)
}

object SysLogField extends TSysLogField
