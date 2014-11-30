package net.shantitree.flow.gaia.qry.view

/**
 * Created by bkkoo on 15/10/2557.
 */

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.base.partner.model.PartnerField
import net.shantitree.flow.gaia.qry.base.MemberBase
import net.shantitree.flow.slick.qry.View
import PartnerField._

object MemberView extends View[MemberBase] {
  def query(base: MemberBase#Q) = {
    base.map { m => (
      member_code -> m.cdmember,
      sponsor_code -> m.cdsponser,
      name -> m.szfullname.trim,
      created_at -> m.dtcreate,
      position -> m.cdpersg.trim
    )}
  }
}
