package net.shantitree.flow.gaia.qry.view

import net.shantitree.flow.base.bznet.model.AcpPositionField
import net.shantitree.flow.gaia.qry.base.AcpPositionBase
import net.shantitree.flow.slick.qry.View
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import AcpPositionField._

object AcpPositionView extends View[AcpPositionBase]{
  def query(base: AcpPositionBase#Q) = {
    base
      .map { case (((t, (positionCode, positionDesc)),(promotedCode, _))) =>(
        member_code -> t.cdmember.trim,
        position -> positionCode.szkey.trim,
        promoted -> promotedCode.szkey.trim,
        start_at -> t.dtstrdate,
        created_at -> t.dtcreate
      )}
  }
}
