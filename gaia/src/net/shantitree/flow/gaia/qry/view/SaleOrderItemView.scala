package net.shantitree.flow.gaia.qry.view

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.base.saleorder.model.SaleOrderItemField
import net.shantitree.flow.gaia.qry.base.SaleOrderItemBase
import net.shantitree.flow.slick.qry.View
import SaleOrderItemField._

object SaleOrderItemView extends View[SaleOrderItemBase] {
  def query(base: SaleOrderItemBase#Q) = {
    base
      .map { case  ((t, (_, unit)), product) => (
        order_code -> t.cdorderno.trim, //order number
        no -> t.iserialno, //line number ordered (lno)
        code -> t.szprodid.trim, //product code
        desc -> t.szprodname.trim, //product description
        qty -> t.iprodqty, //order quantity
        uom -> unit.szdesc.trim, //m.cdunitsale unit of measurement (UOM)
        unit_price -> t.fsaleprice, //unit price
        discount -> t.fdiscountprice, //discount unit price
        currency -> t.cdcurrency.trim, //currency
        cpv -> product.ipvpointbonus * t.iprodqty, //commission PV (CPV)
        qpv -> t.ipvpointtotal, //qualification PV (QPV)
        ppv -> t.ipvpointuptotal //position  PV (PPV)
    )}
  }
}
