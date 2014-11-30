package net.shantitree.flow.slick.qry.param

import org.joda.time.DateTime
case class ModifiedDuration(from: DateTime, until: DateTime) extends Duration
