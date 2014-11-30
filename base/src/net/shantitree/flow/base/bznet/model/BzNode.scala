package net.shantitree.flow.base.bznet.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil
import BzNodeField._
import org.joda.time.DateTime

object BzNode extends ModelDef[BzNode] {
  override def createIndexes(util: IndexUtil[BzNode]) = {
    import util._
    indexFor(UNIQUE_HASH_INDEX, member_code)
    indexFor(NOTUNIQUE_HASH_INDEX, sponsor_code)
    indexForeach(NOTUNIQUE, created_ym, created_on, position)
  }

}

case class BzNode(
  rid: Option[AnyRef] = None,
  member_code: String,
  sponsor_code: String,
  position: Int,
  created_at: Date =  DateTime.now.toDate
) extends Model {
  lazy val created_ym:Int = created_at.toYearMonthInt
  lazy val created_on:Int = created_at.toDateOnlyInt
}
