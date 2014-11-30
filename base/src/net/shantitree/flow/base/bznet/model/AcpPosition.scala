package net.shantitree.flow.base.bznet.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil


object AcpPosition
  extends ModelDef[AcpPosition] {

  import AcpPositionField._
  override def createIndexes(util: IndexUtil[AcpPosition]): Unit = {
    import util._

    indexFor(NOTUNIQUE_HASH_INDEX,  member_code )
    compose(UNIQUE, member_code, position, start_on)
    compose(NOTUNIQUE, member_code, position)
  }

}

case class AcpPosition(
  member_code: String,
  position: Int,
  promoted: Int,
  start_at: Date,
  created_at: Date
) extends Model {
  val rid = None
  val start_on = start_at.toDateOnlyInt
}


