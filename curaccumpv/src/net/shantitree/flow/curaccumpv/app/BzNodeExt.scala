package net.shantitree.flow.curaccumpv.app

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.base.bznet.model.BzNodeVW
import net.shantitree.flow.curaccumpv.model.CurAccumPVVW

object BzNodeExt {
  implicit def fromBzNodeVWtoBzNodeExt(vw: BzNodeVW): BzNodeExt = new BzNodeExt(vw)
}

class BzNodeExt(vw: BzNodeVW) {
  def curAccumPV(comPeriod: Integer)(implicit g: OrientGraph):Option[CurAccumPVVW] = CurAccumPVDML.get(vw, comPeriod)
}
