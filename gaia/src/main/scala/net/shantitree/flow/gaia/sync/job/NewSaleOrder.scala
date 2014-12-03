package net.shantitree.flow.gaia.sync.job

import net.shantitree.flow.base.biz.constant.MiscConst
import net.shantitree.flow.base.saleorder.sys.SaleOrderDML
import net.shantitree.flow.base.saleorder.model.{SaleOrder, SaleOrderItem, SaleOrderHeaderField, SaleOrderHeader}
import net.shantitree.flow.gaia.qry.view.{SaleOrderItemView, SaleOrderHeaderView}
import net.shantitree.flow.gaia.qry.base.{SaleOrderItemBase, SaleOrderHeaderBase}
import net.shantitree.flow.dbsync.conversion.{CompoundDataNormalizer, PCConverter}
import net.shantitree.flow.dbsync.job.{Job, Updater}
import net.shantitree.flow.dbsync.puller.{ParentChildrenPuller, Puller}

object NewSaleOrder
  extends Job[SaleOrderHeaderBase, SaleOrder] {

  private val f = SaleOrderHeaderField

  def createPuller(baseQry: SaleOrderHeaderBase) = {
    val parent = Puller(baseQry, SaleOrderHeaderView)
    val child = Puller(_: SaleOrderItemBase, SaleOrderItemView)
    new ParentChildrenPuller(parent, child, SaleOrder)
  }

  def createUpdater(datum: List[SaleOrder]) = Updater { implicit g =>
      datum.map { saleOrder =>
        SaleOrderDML.addNew(saleOrder)._1.rid
      }
  }

  val modelConverter = PCConverter(SaleOrder, SaleOrderHeader, SaleOrderItem, CompoundDataNormalizer{
    case ("parent", mRow) => Map(

      f.com_period -> {
        val optYear = mRow("com_year").asInstanceOf[Option[String]]
        val optMonth = mRow("com_month").asInstanceOf[Option[String]]

        (optYear, optMonth) match {

          case (Some(year), Some(month)) =>
            val com_period = year + month + "01"

            try {
              com_period.toInt
            } catch { case e:Exception =>
              MiscConst.UnknownCommissionPeriod
            }

          case _ =>
            MiscConst.UnknownCommissionPeriod
        }
      }
      , f.is_return -> {
        val value = mRow("is_return").asInstanceOf[String]
        value == MiscConst.SaleOrderReturnCodeInGaia
      }
      , f.return_ref_code -> {
        if ( mRow(f.is_return).asInstanceOf[String] == MiscConst.SaleOrderReturnCodeInGaia ) {
          mRow(f.return_ref_code)
        } else {
          ""
        }
      }

    )
    case ("child", mRow)=>
      mRow
  })


}
