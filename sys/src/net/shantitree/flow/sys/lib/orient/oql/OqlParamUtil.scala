package net.shantitree.flow.sys.lib.orient.oql

import java.util.Date

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object OqlParamUtil {

  val oDateFormatStr = "yyyy-MM-dd HH:mm:ss"
  val dateFormat = DateTimeFormat.forPattern(oDateFormatStr)

  object OqlLiteral extends Enumeration {

    type OqlLiteral = Value

    val `true`  = Value("true")
    val `false` = Value("false")
  }

  object odate {

    def apply(ISO8601: String): String = s"'${dateFormat.print(new DateTime(ISO8601))}'"
    def apply(datetime: DateTime):String = s"'${dateFormat.print(datetime)}'"
    def apply(date: Date):String = s"'${dateFormat.print(new DateTime(date))}'"

  }

}
