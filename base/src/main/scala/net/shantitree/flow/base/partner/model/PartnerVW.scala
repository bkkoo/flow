package net.shantitree.flow.base.partner.model

import java.lang.Boolean
import java.util.Date

import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper}

object PartnerVW extends VWrap[Partner, PartnerVW]{
  def apply(v: Vertex) = new PartnerVW(v)
}

class PartnerVW(val v: Vertex) extends TVertexWrapper[Partner] {

  val f = PartnerField

  def supplier_code = getString(f.supplier_code)
  def set_supplier_code(value: String) = setString(f.supplier_code, value)

  def company_code = getString(f.company_code)
  def set_company_code(value: String) = setString(f.company_code, value)

  def is_supplier = getBoolean(f.is_supplier)
  def set_is_supplier(value: Boolean) = setBoolean(f.is_supplier, value)

  def is_employee = getBoolean(f.is_employee)
  def set_is_employee(value: Boolean) = setBoolean(f.is_employee, value)

  def is_member = getBoolean(f.is_member)
  def set_is_member(value: Boolean) = setBoolean(f.is_member, value)

  def created_at = getDate(f.created_at)
  def set_created_at(value: Date) = {
    setProperties(f.created_at -> value, f.created_on -> value.toDateOnlyInt)
  }

  def created_on = getInt(f.created_on)

  def sponsor_code = getString(f.sponsor_code)
  def set_sponsor_code(value: String) = setString(f.sponsor_code, value)

  def member_code = getString(f.member_code)
  def set_member_code(value: String) = setString(f.member_code, value)

  def is_current_member = getBoolean(f.is_current_member)
  def set_is_current_member(value: Boolean) = setBoolean(f.is_current_member, value)

  def employee_code = getString(f.employee_code)
  def set_employee_code(value: String) = setString(f.employee_code, value)


  def is_org = getBoolean(f.is_org)
  def set_is_org(value: Boolean) = setBoolean(f.is_org, value)


  def code = getString(f.code)
  def set_code(value: String) = setString(f.code, value)

  def name = getString(f.name)
  def set_name(value: String) = setString(f.name, value)

}
