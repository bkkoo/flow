package net.shantitree.flow.slick.qry.param

import java.sql.Timestamp
import org.joda.time.DateTime

trait Duration extends Param {
  val from: DateTime
  val until: DateTime
  lazy val fromTimestamp = new Timestamp(from.getMillis)
  lazy val untilTimestamp = new Timestamp(until.getMillis)
  lazy val rangeTimestamp = (fromTimestamp, untilTimestamp)
}
