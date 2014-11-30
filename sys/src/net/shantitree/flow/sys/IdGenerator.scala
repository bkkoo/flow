package net.shantitree.flow.sys

object IdGenerator {

  def genPartnerCode() = DateTime.now.toString("yyyyMMddHHmmssSSS")

}
