package net.shantitree.flow.sys

object JapinComPeriodUtil extends ComPeriodUtil {

  // will be read from config file instead
  lazy val comPeriodExtendedByDays:Int = 7

  // Current Japin's commission interval is monthly basis
  // so set interval 'id' to 1 without any calc should be sufficient.
  // note: com period id will be "year(0)month(0)interval".toInt
  protected def genComIntervalId(date: DateTime): Int = 1

}
