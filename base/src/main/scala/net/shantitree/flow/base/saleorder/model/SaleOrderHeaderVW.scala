package net.shantitree.flow.base.saleorder.model

import java.util.Date

import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.saleorder.sys.{SaleOrderBL, SaleOrderDML}
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper}

object SaleOrderHeaderVW extends VWrap[SaleOrderHeader, SaleOrderHeaderVW]{
  def apply(v: Vertex) = new SaleOrderHeaderVW(v)
}

class SaleOrderHeaderVW(val v: Vertex) extends TVertexWrapper[SaleOrderHeader] {
  val f = SaleOrderHeaderField

  def partner_code = getString(f.partner_code)
  def set_partner_code(value: String) = setString(f.partner_code, value)

  def issued_on = getInt(f.issued_on)

  def issued_at = getDate(f.issued_at)
  def set_issued_at(value: Date) = {
    setProperties(f.issued_at -> value, f.issued_on -> issued_at.toDateOnlyInt, f.issued_ym -> issued_at.toYearMonthInt)
  }

  def issued_ym = getInt(f.issued_ym)

  def com_period = getInt(f.com_period)
  def set_com_period(value: Integer) = set[Integer](f.com_period, value)

  def buyer_name = getString(f.buyer_name)
  def set_buyer_name(value: String) = setString(f.buyer_name, value)

  def member_code = getString(f.member_code)
  def set_member_code(value: String) = setString(f.member_code, value)

  def code = getString(f.code)
  def set_code(value: String) = setString(f.code, value)

  def is_return = getBoolean(f.is_return)
  def set_is_return(value: Boolean) = setBoolean(f.is_return, value)

  def return_ref_code = getString(f.return_ref_code)
  def set_return_ref_code(value: String) = setString(f.return_ref_code, value)

  def items()(implicit g: OrientGraph) = SaleOrderDML.getItems(this)
  def pv()(implicit g: OrientGraph) = SaleOrderBL.getPV(this)
  def bzNode()(implicit g: OrientGraph) = SaleOrderBL.getBzNode(this)

}

