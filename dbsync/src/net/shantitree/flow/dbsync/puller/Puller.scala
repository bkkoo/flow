package net.shantitree.flow.dbsync.puller

import net.shantitree.flow.slick.qry.{BaseQry, View}

case class Puller[Q <: BaseQry](baseQry: Q, view: View[Q]) extends TPuller[Q]
