package net.shantitree.flow.curaccumpv.model

import java.util.Date

import com.orientechnologies.orient.core.metadata.schema.OClass.INDEX_TYPE._
import com.tinkerpop.blueprints.Vertex
import net.shantitree.flow.sys.lib.{TSysLogVW, TSysLogField}
import net.shantitree.flow.sys.lib.model.{Model, ModelDef, TVertexWrapper, VWrap}
import net.shantitree.flow.sys.lib.orient.model.IndexUtil
import net.shantitree.flow.lib.TSysLogVW
import org.joda.time.DateTime

object CurAccumPVLog extends ModelDef[CurAccumPVLog] {

  val F = CurAccumPVLogField

  override def createIndexes(util: IndexUtil[CurAccumPVLog]): Unit = {
    import util._
    indexForeach(NOTUNIQUE_HASH_INDEX, F.com_period, F.to_code, F.status)
    compose(NOTUNIQUE, F.com_period, F.to_code)
  }

}

case class CurAccumPVLog(
  rid: Option[AnyRef]=None
  ,com_period: Int
  ,from_code: String = ""
  ,to_code: String = ""
  ,created_at: Date = DateTime.now.toDate
  ,status: String = CurAccumPVLogStatus.Processing
) extends Model

object CurAccumPVLogVW extends VWrap[CurAccumPVLog, CurAccumPVLogVW]{
  def apply(v: Vertex) = new CurAccumPVLogVW(v)
}

class CurAccumPVLogVW(val v: Vertex) extends TVertexWrapper[CurAccumPVLog] with TSysLogVW {

  val F = CurAccumPVLogField

  def com_period = getInt(F.com_period)
  def set_com_period(value: Integer) = setInt(F.com_period, value)

  def from_code = getString(F.from_code)
  def set_from_code(value: String) = setString(F.from_code, value)

  def to_code = getString(F.to_code)
  def set_to_code(value: String) = setString(F.to_code, value)

  def set_status(value: String) = setString(F.status, value)
  def status = getString(F.status)

  def setToSuccess() = setString(F.status, CurAccumPVLogStatus.Success)
  def setToProcessing() = setString(F.status, CurAccumPVLogStatus.Processing)
  def setToFailure() = setString(F.status, CurAccumPVLogStatus.Failure)

}

object CurAccumPVLogField extends TSysLogField {
  val com_period = "com_period"
  val from_code = "from_code"
  val to_code = "to_code"
  val status = "status"
}

object CurAccumPVLogStatus {
  val Processing = "processing"
  val Success = "success"
  val Failure = "failure"
}
