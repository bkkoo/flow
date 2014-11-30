package net.shantitree.flow.base.bznet.model

import java.util.{Map => JMap}

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.model.{VWrap, Model, ModelDef, TVertexWrapper}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil

object PeriodicAccumulatedPV extends ModelDef[PeriodicAccumulatedPV] {

  import net.shantitree.flow.base.bznet.model.PeriodicAccumulatedPVField._

  override def createIndexes(util:IndexUtil[PeriodicAccumulatedPV]) {
    import util._
    compose(NOTUNIQUE_HASH_INDEX, member_code, year)
  }

}

case class PeriodicAccumulatedPV(
  rid: Option[AnyRef]=None
  ,year: Int
  ,member_code: String
  ,p01: JMap[String, Long]
  ,p02: JMap[String, Long]
  ,p03: JMap[String, Long]
  ,p04: JMap[String, Long]
  ,p05: JMap[String, Long]
  ,p06: JMap[String, Long]
  ,p07: JMap[String, Long]
  ,p08: JMap[String, Long]
  ,p09: JMap[String, Long]
  ,p10: JMap[String, Long]
  ,p11: JMap[String, Long]
  ,p12: JMap[String, Long]
) extends Model

object PeriodicAccumulatedPVField {
  val member_code = "member_code"
  val year = "year"
  val p01 = "p01"
  val p02 = "p02"
  val p03 = "p03"
  val p04 = "p04"
  val p05 = "p05"
  val p06 = "p06"
  val p07 = "p07"
  val p08 = "p08"
  val p09 = "p09"
  val p10 = "p10"
  val p11 = "p11"
  val p12 = "p12"
}

object PeriodicAccumulatedPVVW extends VWrap[PeriodicAccumulatedPV, PeriodicAccumulatedPVVW]{
  def apply(v: Vertex) = new PeriodicAccumulatedPVVW(v)
}
class PeriodicAccumulatedPVVW(val v: Vertex) extends TVertexWrapper[PeriodicAccumulatedPV] {

  val f = PeriodicAccumulatedPVField

  def year: Integer = getInt(f.year)
  def set_year(value: Integer) = setInt(f.year, value)
  def member_code: String = getString(f.member_code)
  def set_member_code(value: String) = setString(f.member_code, value)

  def p01: JMap[String, Long] = get[JMap[String, Long]](f.p01)
  def set_p01(value: JMap[String, Long]) = set[JMap[String, Long]](f.p01, value)

  def p02: JMap[String, Long] = get[JMap[String, Long]](f.p02)
  def set_p02(value: JMap[String, Long]) = set[JMap[String, Long]](f.p02, value)

  def p03: JMap[String, Long] = get[JMap[String, Long]](f.p03)
  def set_p03(value: JMap[String, Long]) = set[JMap[String, Long]](f.p03, value)

  def p04: JMap[String, Long] = get[JMap[String, Long]](f.p04)
  def set_p04(value: JMap[String, Long]) = set[JMap[String, Long]](f.p04, value)

  def p05: JMap[String, Long] = get[JMap[String, Long]](f.p05)
  def set_p05(value: JMap[String, Long]) = set[JMap[String, Long]](f.p05, value)

  def p06: JMap[String, Long] = get[JMap[String, Long]](f.p06)
  def set_p06(value: JMap[String, Long]) = set[JMap[String, Long]](f.p06, value)

  def p07: JMap[String, Long] = get[JMap[String, Long]](f.p07)
  def set_p07(value: JMap[String, Long]) = set[JMap[String, Long]](f.p07, value)

  def p08: JMap[String, Long] = get[JMap[String, Long]](f.p08)
  def set_p08(value: JMap[String, Long]) = set[JMap[String, Long]](f.p08, value)

  def p09: JMap[String, Long] = get[JMap[String, Long]](f.p09)
  def set_p09(value: JMap[String, Long]) = set[JMap[String, Long]](f.p09, value)

  def p10: JMap[String, Long] = get[JMap[String, Long]](f.p10)
  def set_p10(value: JMap[String, Long]) = set[JMap[String, Long]](f.p10, value)

  def p11: JMap[String, Long] = get[JMap[String, Long]](f.p11)
  def set_p11(value: JMap[String, Long]) = set[JMap[String, Long]](f.p11, value)

  def p12: JMap[String, Long] = get[JMap[String, Long]](f.p12)
  def set_p12(value: JMap[String, Long]) = set[JMap[String, Long]](f.p12, value)
}
