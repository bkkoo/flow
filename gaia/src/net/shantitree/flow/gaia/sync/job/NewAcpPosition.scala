package net.shantitree.flow.gaia.sync.job

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.app.AcpPositionDML
import net.shantitree.flow.base.bznet.constant.{MemberDataJournalTypeConst, MemberPositionConst}
import net.shantitree.flow.base.bznet.model.{AcpPositionField, AcpPosition}
import net.shantitree.flow.gaia.qry.base.AcpPositionBase
import net.shantitree.flow.gaia.qry.view.AcpPositionView
import net.shantitree.flow.dbsync.conversion.{SMConverter, DataNormalizer}
import net.shantitree.flow.dbsync.job.{Updater, Job}
import net.shantitree.flow.dbsync.puller.Puller

object NewAcpPosition extends Job[AcpPositionBase, AcpPosition]{

  val f = AcpPositionField

  def createPuller(baseQry: AcpPositionBase) = Puller(baseQry, AcpPositionView)

  /*----------------------------------------------------------------------------*/
  val modelConverter = SMConverter(AcpPosition, DataNormalizer { mRow => Map(

    f.position -> {try {
      val value = mRow(f.position).asInstanceOf[String].toInt
      if (MemberPositionConst.isPosition(value)) {
        value
      } else {
        MemberPositionConst.UnknownPos
      }
    } catch {
      case e: Exception => MemberPositionConst.UnknownPos
    }} ,

    f.promoted -> { try {
      val value =  mRow(f.promoted).asInstanceOf[String].toInt
      if (MemberDataJournalTypeConst.isJournalType(value)) {
        value
      } else {
        MemberDataJournalTypeConst.UnknownType
      }
    } catch {
      case e: Exception => MemberDataJournalTypeConst.UnknownType
    }}

  )})

  /*----------------------------------------------------------------------------*/
  private def isDuplicate(b: AcpPosition, a: AcpPosition): Boolean = {
    b.position == a.position && b.start_on == a.start_on
  }

  private def isExist(a: AcpPosition)(implicit g: OrientGraph): Boolean = {
    AcpPositionDML.findPositionOnDate(a.member_code, a.position, a.start_on).nonEmpty
  }


  def createUpdater(datum: List[AcpPosition]) = {
    /*  remove duplicate record from result set which has
        the same set of  'member code' and 'position' and 'on' (start date) */
    val groupedDatum = datum
      .groupBy(_.member_code)
      .map { case (k, l) =>
      k -> l.sortBy { m => (m.start_on, m.position) }
    }

    Updater { implicit g =>

      groupedDatum.map { case (k, l) =>

        k -> l.foldLeft(List[AcpPosition]()) {
          case (Nil, a) if isExist(a) => Nil
          case (Nil, a) => a::Nil
          case (b@m::ms, a) if isDuplicate(m, a) => b
          case (b, a) if isExist(a) => b
          case (b, a) => a::b
        }
      }.foreach { case (k, l) =>
        AcpPositionDML.addNew(k, l)
      }
    }
  }

}
