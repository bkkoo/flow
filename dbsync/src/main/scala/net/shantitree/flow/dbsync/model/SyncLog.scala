package net.shantitree.flow.dbsync.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import net.shantitree.flow.sys.lib.model.{Model, ModelDef}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import org.joda.time.DateTime

object SyncLog extends ModelDef[SyncLog] {

  override def createIndexes(util: IndexUtil[SyncLog]) {
    import util._
    compose(NOTUNIQUE_HASH_INDEX, "job_runner", "result")
  }

  object SType {
    val Bulk = "bulk"
    val Periodical = "periodical"
  }

  object SResult {
    val Success = "success"
    val Failure = "failure"
    val Promise = "promise"
  }

  object Fields {
    val job_runner = "job_runner"
    val from = "from"
    val until = "until"
    val first_code = "first_code"
    val last_code = "last_code"
    val start_at = "start_at"
    val end_at = "end_at"
    val result = "result"
    val sync_type = "periodical"
    val note = "note"
  }

}


case class SyncLog (
  rid: Option[AnyRef]=None,
  job_runner: String,
  from: Option[Date]=None,
  until: Option[Date]=None,
  first_code: Option[String]=None,
  last_code: Option[String]=None,
  start_at: Date=DateTime.now.toDate,
  end_at: Option[Date]=None,
  result: String="promise",
  sync_type: String="unknown",
  note: Option[String]=None
) extends Model {
  override def toString =
    s"""
      |job runnerRef: $job_runner
      |   from -> until : ${from.map(_.toJoda.toLocalDateTime.toString).getOrElse("_")} -> ${until.map(_.toJoda.toLocalDateTime.toString).getOrElse("_")}
      |   first code -> last code: ${first_code.getOrElse("-")} -> last code: ${last_code.getOrElse("-")}
      |   start at -> end at: ${start_at.toJoda.toLocalDateTime.toString} -> ${end_at.map(_.toJoda.toLocalDateTime.toString).getOrElse("-")}
      |   result: $result
      |   note: ${note.getOrElse("-")}
      |
    """.stripMargin
}
