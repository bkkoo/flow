package net.shantitree.flow.base.bznet.constant

object MemberDataJournalTypeConst {

  val UnknownType = -1
  val ApplyingForMemberShip = 1
  val MembershipTransferring = 2
  val InterRecommended = 3
  val OperationsTransferring = 4
  val ChangingSponsor = 5
  // In GAIA: Personal/Company Change  = 6
  val MembershipCancellation = 7
  val ILifeMembershipTransferring = 8
  val ManuallyPromoted = 9
  val Demoted = 10
  val AutoPromoted = 11
  val MembershipRenewal = 12
  val AutoMembershipRenewal = 98
  val HistoricalData = 99

  val setOfJournalType = Set(
    ApplyingForMemberShip
    ,MembershipTransferring
    ,InterRecommended
    ,OperationsTransferring
    ,ChangingSponsor
    ,MembershipCancellation
    ,ILifeMembershipTransferring
    ,ManuallyPromoted
    ,Demoted
    ,AutoPromoted
    ,MembershipRenewal
    ,AutoMembershipRenewal
    ,HistoricalData
    ,UnknownType
  )

  def isJournalType(value: Int) = setOfJournalType(value)
}
