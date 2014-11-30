package net.shantitree.flow.base.partner.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.base.partner.app.PartnerIdGen
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.conversion.{NoneNotDataVal, OptionNotDataVal}
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil
import org.joda.time.DateTime


object Partner extends ModelDef[Partner] {

  import PartnerField._
  override def createIndexes(util: IndexUtil[Partner]) {
    import util._

    indexForeach(NOTUNIQUE_HASH_INDEX,
      code,
      member_code,
      supplier_code,
      employee_code,
      sponsor_code,
      name,
      is_member,
      is_current_member
    )

    indexForeach(NOTUNIQUE, created_on)
    compose(NOTUNIQUE, is_member, is_current_member, created_on)
    compose(NOTUNIQUE, is_member, created_on)

  }

}

case class Partner (
  rid: Option[AnyRef]=None,
  code: String = PartnerIdGen.genId(),
  member_code:Option[String]=None,
  supplier_code: Option[String]=None,
  employee_code: Option[String]=None,
  company_code:Option[String]=None,
  sponsor_code: Option[String]=None,
  name: Option[String]=None,
  is_member: Boolean=false,
  is_supplier: Boolean=false,
  is_employee: Boolean=false,
  is_current_member: Boolean=false,
  is_org: Boolean=false,
  created_at: Date = DateTime.now.toDate,
  position: OptionNotDataVal[Int] = NoneNotDataVal //use only for catch data from GAIA sync
) extends Model {
  lazy val created_on:Int = created_at.toDateOnlyInt
}
