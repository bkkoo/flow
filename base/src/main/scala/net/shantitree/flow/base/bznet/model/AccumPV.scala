package net.shantitree.flow.base.bznet.model

import java.util.{Map => JMap}

import net.shantitree.flow.sys.lib.model.{Model, ModelDef, TVertexWrapper}

import scala.collection.JavaConverters._

object AccumPV extends ModelDef[AccumPV]

case class AccumPV(
  rid: Option[AnyRef]=None
  ,year: Int
  ,member_code: String
  ,month: Int
  ,ppv: Long
  ,qpv: Long
  ,cpv: Long
) extends Model

object AccumPVField {
  val member_code = "member_code"
  val year = "year"
  val month = "month"
  val qpv = "qpv"
  val ppv = "ppv"
  val cpv = "cpv"
}

case class AccumPVVW(record: PeriodicAccumulatedPVVW, month: Int) extends TVertexWrapper[AccumPV] {

  val v = record.v
  val f = AccumPVField

  lazy val monthStr: String = if (month > 9 ) { month.toString } else { s"0${month.toString}"}
  lazy val period: String = s"p$monthStr"
  protected lazy val pvTypes: List[String] = List(f.ppv, f.qpv, f.cpv)

  def year: String = getString(f.year)
  def member_code: String = getString(f.member_code)
  def ppv: Long = getPVof(f.ppv)
  def set_ppv(value: Long) = setPV(f.ppv, value)
  def qpv: Long = getPVof(f.qpv)
  def set_qpv(value: Long) = setPV(f.qpv, value)
  def cpv: Long = getPVof(f.cpv)
  def set_cpv(value: Long) = setPV(f.cpv, value)

  def setPV(ppvValue: Long, qpvValue: Long, cpvValue: Long) = {
    record.set[JMap[String, Long]](period, Map(f.ppv -> ppvValue, f.qpv -> qpvValue, f.cpv -> cpvValue).asJava)
  }
  
  protected def getPVof(pvType: String): Long = {
    record.get[JMap[String, Long]](period).asScala.getOrElse(pvType, 0)
  }
  

  protected def setPV(pvType: String, value: Long): Boolean = {
    val oldValue = record.get[JMap[String, Long]](period)
    if (oldValue == null) {
      val newValue = (pvType -> value)::pvTypes.filterNot(_ == pvType).map( _ -> 0.asInstanceOf[Long])
      record.v.setProperty(period, newValue.toMap[String, Long].asJava)
      true
    } else {
      if (oldValue.containsKey(pvType) && oldValue.get(pvType) != value) {
        oldValue.put(pvType, value)
        record.v.setProperty(period, oldValue)
        true
      } else {
        false
      }
    }
  }

}

