package net.shantitree.flow.gaia.qry.view

import net.shantitree.flow.base.bznet.model.AccumPVField
import net.shantitree.flow.gaia.qry.base.PeriodicAccumulatedPVBase
import net.shantitree.flow.slick.qry.View
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import AccumPVField._

object PeriodicAccumulatedPVView extends View[PeriodicAccumulatedPVBase]{
  def query(base: PeriodicAccumulatedPVBase#Q) = {
    base.map { m => (
       member_code -> m.cdmember
      ,year -> m.szdatayear
      ,month -> m.szdatamonth
      ,ppv -> m.icumuppv
      ,qpv -> m.icumpv
    )}
  }

}
