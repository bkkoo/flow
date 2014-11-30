package net.shantitree.flow.gaia.qry.base


object AcpPositionBase extends A0001QryCondition[AcpPositionBase] {
  def apply() = new AcpPositionBase {}
  def apply(q: AcpPositionBase#Q) = new AcpPositionBase { override lazy val query = q }
}

trait AcpPositionBase extends  A0001Base {
  // Member's position upgraded/promoted journal/alteration types in GAIA is 09, 10, 11
  val statusJournalTypes = List("09", "10", "11")
}
