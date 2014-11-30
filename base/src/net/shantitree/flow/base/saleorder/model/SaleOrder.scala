package net.shantitree.flow.base.saleorder.model

import net.shantitree.flow.sys.lib.model.{ParentChildrenModel, ParentChildrenModelDef}

object SaleOrder extends ParentChildrenModelDef[SaleOrder] {
  val pf = SaleOrderHeaderField
  val cf = SaleOrderItemField

  val masterKey = pf.code
  val foreignKey = cf.order_code
  val parentFieldName = "header"
  val childrenFieldName = "items"
}

case class SaleOrder(
  header: SaleOrderHeader,
  items: List[SaleOrderItem]
) extends ParentChildrenModel(header, items)
