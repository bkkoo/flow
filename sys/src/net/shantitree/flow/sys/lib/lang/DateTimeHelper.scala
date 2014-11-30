package net.shantitree.flow.sys.lib.lang

import java.util.{Date => JDate}

import org.joda.time.{Duration, DateTime}

object DateTimeHelper {

  implicit def javaDateToJoda1(date: JDate): DateTime = new DateTime(date)
  implicit def javaDateToJoda2(date: JDate): JavaDateToJoda = new JavaDateToJoda(date)
  implicit def jodaToJodaHelper(date: DateTime): JodaHelper = new JodaHelper(date)

  class JavaDateToJoda(date: JDate) {
    def toJoda = new DateTime(date)
    def toDateOnlyInt:Int = toJoda.toString("yyyyMMdd").toInt
    def toYearInt = toJoda.getYear
    def toMonthInt = toJoda.getMonthOfYear
    def toYearMonthInt = toJoda.toString("yyyyMM").toInt
  }

  class JodaHelper(date: DateTime) {

    def toDateOnlyInt:Int = date.toString("yyyyMMdd").toInt
    def toYearMonthInt:Int = date.toString("yyyyMM").toInt

    def firstDate:DateTime = {
      val month = date.getMonthOfYear
      val year = date.getYear
      new DateTime(s"${year.toString}${month.toString}01T00:00:00")
    }

    def durationUntilNow:Duration = new Duration(date, DateTime.now)
    def durationUntil(untilDateTime: DateTime) = new Duration(date, untilDateTime)
  }

}

