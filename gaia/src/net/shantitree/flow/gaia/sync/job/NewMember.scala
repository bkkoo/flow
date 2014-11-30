package net.shantitree.flow.gaia.sync.job

import net.shantitree.flow.base.bznet.app.MemberDML
import net.shantitree.flow.base.bznet.constant.MemberPositionConst
import net.shantitree.flow.base.partner.model.{PartnerField, Partner}
import net.shantitree.flow.gaia.qry.base.MemberBase
import net.shantitree.flow.gaia.qry.view.MemberView
import net.shantitree.flow.dbsync.conversion.{SMConverter, DataNormalizer}
import net.shantitree.flow.dbsync.job.{Job, Updater}
import net.shantitree.flow.dbsync.puller.Puller
import net.shantitree.flow.sys.IdGenerator


object NewMember
  extends Job[MemberBase, Partner] {

  val f = PartnerField

  def createPuller(base: MemberBase) = Puller(base, MemberView)
  def createUpdater(datum:List[Partner]) =  Updater { implicit g =>
    MemberDML.addNew(datum)
  }

  import MemberPositionConst._

  val modelConverter = SMConverter(Partner, DataNormalizer { mRow => Map(
    f.is_member -> true,
    f.is_current_member -> true,
    f.code -> IdGenerator.genPartnerCode(),
    f.position -> {try {
      val value = mRow(f.position).asInstanceOf[String].toInt
      if (isMemberPosition(value)) { value } else { UnknownPos }
    } catch {
      case e: Exception => UnknownPos
    }}
  )})


}
