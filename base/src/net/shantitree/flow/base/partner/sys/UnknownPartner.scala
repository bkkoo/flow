package net.shantitree.flow.base.partner.sys

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.constant.BNodeCodeConst
import net.shantitree.flow.base.partner.model.{Partner, PartnerField, PartnerVW}
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import BNodeCodeConst._

object UnknownPartner {

  private val F = PartnerField

  private def vwPartner(implicit g: OrientGraph) = {
    getV() match {
      case Some(v) => PartnerVW(v)
      case None => throw new RuntimeException("Unknown partner not found!")
    }

  }

  def getV()(implicit g: OrientGraph) = g.findV[Partner](F.code -> UnknownCode)
  def get()(implicit g: OrientGraph) = vwPartner
  def init()(implicit g: OrientGraph) = {
    getV() match {
      case None =>
        g.addV(Partner(code = UnknownCode ))
      case Some(_) =>
    }
  }


}
