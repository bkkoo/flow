package net.shantitree.flow.base.saleorder.model

import java.lang.{Double => JDouble}
import java.math.{BigDecimal => JBigDecimal}

import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.model.{VWrap, TVertexWrapper}

object SaleOrderItemVW extends VWrap[SaleOrderItem, SaleOrderItemVW]{
  def apply(v: Vertex) = new SaleOrderItemVW(v)
}

class SaleOrderItemVW(val v: Vertex) extends TVertexWrapper[SaleOrderItem] {

  def qpv = getInt("qpv")
  def set_qpv(value: Integer) =  setInt("qpv", value)

  def discount_unit_price = getBigDecimal("discount")
  def set_discount_unit_price(value: JBigDecimal) = setBigDecimal("discount", value)

  def no = getInt("no")
  def set_no(value: Integer) = setInt("no", value)

  def desc = getString("desc")
  def set_desc(value: String) = setString("desc", value)

  def cpv = getInt("cpv")
  def set_cpv(value: Integer) = setInt("cpv", value)

  def qty = v.getProperty[JDouble]("qty")
  def set_qty(value: JDouble) = setDouble("qty", value)

  def unit_price = get[JBigDecimal]("unit_price")
  def set_unit_price(value: BigDecimal) = setBigDecimal("unit_price", value)

  def order_code = getString("order_code")
  def set_order_code(value: String) = setString("order_code", value)

  def ppv = getInt("ppv")
  def set_ppv(value: Integer) = setInt("ppv", value)

  def currency = getString("currency")
  def set_currency(value: String) = setString("currency", value)

  def uom = getString("uom")
  def set_uom(value: String) = setString("uom", value)

  def code = getString("code")
  def set_code(value: String) = setString("code", value)

}
