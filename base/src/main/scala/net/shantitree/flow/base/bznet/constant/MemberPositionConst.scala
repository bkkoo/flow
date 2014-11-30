package net.shantitree.flow.base.bznet.constant

object MemberPositionConst {

  // direct sale distributor
  val DirectDistributor = 10
  val Retailer = 20
  val Dealer = 30
  val AuthDealer = 40
  val Agent = 50
  val Supervisor = 60
  val GoldSupervisor = 70

  // system position
  val SeedPos = 0
  val UnknownPos = -1

  val setOfPosition = Set(DirectDistributor, Retailer, Dealer, AuthDealer, Agent, Supervisor, GoldSupervisor, SeedPos, UnknownPos)
  val setOfSystemPosition = Set(SeedPos, UnknownPos)
  val setOfMemberPosition = Set(DirectDistributor, Retailer, Dealer, AuthDealer, Agent, Supervisor, GoldSupervisor)

  def isPosition(code: Int): Boolean = setOfPosition(code)
  def isSystemPosition(code: Int) = setOfSystemPosition(code)
  def isMemberPosition(code: Int) = setOfMemberPosition(code)

}
