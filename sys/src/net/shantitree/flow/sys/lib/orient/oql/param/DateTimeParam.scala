package net.shantitree.flow.sys.lib.orient.oql.param

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object DateTimeParam {
  /* orient sql specific format! I've try ISO8601 format and get
    the exception! It accept only this format  "yyyy-MM-dd HH:mm:ss"
  */
  lazy val formatString = "yyyy-MM-dd HH:mm:ss"
  lazy val format = DateTimeFormat.forPattern(formatString)

  def mkParam(dateTime: DateTime) = s"'${format.print(dateTime)}'"

  implicit def stringToParam(ISO8601: String):DateTimeParam = DateTimeParam(new DateTime(ISO8601))
  implicit def dateToParam(date: Date):DateTimeParam = DateTimeParam(new DateTime(date))
  implicit def dateTimeToParam(dateTime: DateTime):DateTimeParam = DateTimeParam(dateTime)

}

case class DateTimeParam(dateTime: DateTime) {
  lazy val param = DateTimeParam.mkParam(dateTime)
  def toDateTimeParam = this
}
