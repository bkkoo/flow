package net.shantitree.flow.base.partner.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.biz.lib.DML
import net.shantitree.flow.base.partner.model.{Partner, PartnerField, PartnerVW}
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._

object PartnerDML extends DML[Partner] {

  private val F = PartnerField

  def findByMemberCode(code: String)(implicit g: OrientGraph): Option[PartnerVW] = {
    g.findV(PartnerVW)(F.member_code -> code)
  }

  def findByCode(code: String)(implicit g: OrientGraph): Option[PartnerVW] = {
    g.findV(PartnerVW)(F.code -> code)
  }
}
