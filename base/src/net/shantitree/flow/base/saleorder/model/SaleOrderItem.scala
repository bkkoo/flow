package net.shantitree.flow.base.saleorder.model

import java.math.{BigDecimal => JavaBigDecimal}

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil

object SaleOrderItem extends ModelDef[SaleOrderItem] {
  override def createIndexes(util: IndexUtil[SaleOrderItem]) {
    import net.shantitree.flow.base.saleorder.model.SaleOrderItemField._
    import util._
    indexForeach(NOTUNIQUE_HASH_INDEX, order_code, code)
  }
}

case class SaleOrderItem (
  rid: Option[AnyRef]=None,
  order_code: String,
  no: Int,
  code: String,
  desc: String,
  qty: Double,
  uom: String,
  unit_price: JavaBigDecimal,
  discount: JavaBigDecimal,
  currency: String,
  cpv: Int,
  qpv: Int,
  ppv: Int
) extends Model
