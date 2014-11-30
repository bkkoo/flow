package net.shantitree.flow.gaia.qry.view

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.base.saleorder.model.SaleOrderHeaderField
import net.shantitree.flow.gaia.qry.base.SaleOrderHeaderBase
import net.shantitree.flow.slick.qry.View
import SaleOrderHeaderField._


object SaleOrderHeaderView extends View[SaleOrderHeaderBase] {
  def query(base: SaleOrderHeaderBase#Q) = {
    base.map { m => (
      code -> m.cdorderno.trim,
      issued_at -> m.dtbusinessdate,
      member_code -> m.cdmember.trim,
      buyer_name ->  m.szname.trim,
      "com_year" -> m.szbonusyear, //Option[String]
      "com_month" -> m.szbonusmonth, //Option[String]
      is_return -> m.cdprefix.trim,
      return_ref_code -> m.cdordernoRoot.trim
    )}
  }
}
