package net.shantitree.flow.base.saleorder.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.base.biz.constant.MiscConst
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.conversion.DataValConverter
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil

object SaleOrderHeader extends ModelDef[SaleOrderHeader] {

  import SaleOrderHeaderField._

  override def createIndexes(util: IndexUtil[SaleOrderHeader]) {
    import util._

    indexFor(UNIQUE_HASH_INDEX, code)
    indexForeach(NOTUNIQUE, issued_on, issued_ym, com_period)
    indexForeach(NOTUNIQUE_HASH_INDEX, member_code, partner_code, is_return, return_ref_code)
    compose(NOTUNIQUE, com_period, code)
  }

  override lazy val dataValConverter = DataValConverter {
    case (`com_period`, (null, _)) => (com_period, MiscConst.UnknownCommissionPeriod)
  }

}

case class SaleOrderHeader(
  rid: Option[AnyRef]=None,
  partner_code: String,
  code: String,
  issued_at: Date,
  member_code: String,
  buyer_name: String,
  com_period: Int=MiscConst.UnknownCommissionPeriod,
  is_return: Boolean=false,
  return_ref_code: String=""
) extends Model {
  lazy val issued_on: Int = issued_at.toDateOnlyInt
  lazy val issued_ym: Int = issued_at.toYearMonthInt
}
