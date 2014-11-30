package net.shantitree.flow.curaccumpv.model

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.PV
import net.shantitree.flow.sys.lib.model.{VWrap, Model, ModelDef, TVertexWrapper}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil

object CurAccumPV extends ModelDef[CurAccumPV] {

  val f = CurAccumPVField

  override def createIndexes(util: IndexUtil[CurAccumPV]) {
    import util._
    indexForeach(NOTUNIQUE_HASH_INDEX, f.member_code, f.com_period)
    compose(UNIQUE_HASH_INDEX, f.member_code, f.com_period)
  }
}

case class CurAccumPV(
  rid: Option[AnyRef]=None
  ,com_period: Int
  ,member_code: String
  ,ppv: Int
  ,qpv: Int
  ,cpv: Int
) extends Model

object CurAccumPVField {
  val member_code = "member_code"
  val com_period = "com_period"
  val qpv = "qpv"
  val ppv = "ppv"
  val cpv = "cpv"
}

object CurAccumPVVW extends VWrap[CurAccumPV, CurAccumPVVW]{
  def apply(v: Vertex) = new CurAccumPVVW(v)
}

class CurAccumPVVW(val v: Vertex) extends TVertexWrapper[CurAccumPV] {

  val f = CurAccumPVField

  def member_code: String = get[String](f.member_code)

  def ppv: Integer = getInt(f.ppv)
  def set_ppv(value: Integer) = setInt(f.ppv, value)

  def qpv: Integer = getInt(f.qpv)
  def set_qpv(value: Integer) = setInt(f.qpv, value)

  def cpv: Integer = getInt(f.cpv)
  def set_cpv(value: Integer) = setInt(f.cpv, value)

  def com_period: Integer = getInt(f.com_period)
  def set_com_period(value: Integer) = setInt(f.com_period, value)

  def setPV(pv: PV) =  {
    set_ppv(pv.ppv)
    set_qpv(pv.qpv)
    set_cpv(pv.cpv)
  }

  def getPV = PV(ppv,qpv, cpv)

}
