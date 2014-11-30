package net.shantitree.flow.base.partner.app

import org.joda.time.DateTime


object PartnerIdGen {

  def genId() = DateTime.now.toString("yyyyMMddHHmmssSSS")

}
