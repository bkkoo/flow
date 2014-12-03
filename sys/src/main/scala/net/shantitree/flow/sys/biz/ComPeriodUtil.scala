package net.shantitree.flow.sys.biz

import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import org.joda.time.DateTime

trait ComPeriodUtil {

   // will be read from config file instead
   val comPeriodExtendedByDays:Int

   def previous:Int = previousFrom(DateTime.now)
   def previousFrom(from: DateTime):Int = {
     //todo: will check for this again!
     genComPeriodId(from.minusMonths(1))
   }

   def current:Int = {
     val now = DateTime.now
     comPeriodOn(now)
   }

   def currentOverlappedComPeriod:Option[Int] = {
     val now = DateTime.now
     overlappedComPeriodOn(now)
   }

   def overlappedComPeriodOn(date: DateTime=DateTime.now):Option[Int] = {
     if (isExtendedOver(date)) {
       None
     } else {
       Some(previousFrom(date))
     }
   }

   def isExtendedOver(date: DateTime) = {
     date.firstDate.durationUntil(date).getStandardDays > comPeriodExtendedByDays
   }

   def comPeriodOn(date: DateTime) = {
     genComPeriodId(date)
   }

   protected def genComPeriodId(date: DateTime): Int = {
     val year = date.getYear
     val month = prefixWithZero(date.getMonthOfYear)
     val interval = prefixWithZero(genComIntervalId(date))
     s"$year$month$interval".toInt
   }

   protected def genComIntervalId(date: DateTime):Int

   protected def prefixWithZero(num: Int): String = {
     require(num > 0)
     if (num < 10 ) { s"0$num" } else { num.toString }
   }

 }
