package net.shantitree.flow.sys.lib.lang


import RuTypeDefinition._
import net.shantitree.flow.sys.lib.model.conversion.NoneNotDataVal
import org.joda.time.DateTime

object TypeDefaultValDefinition extends TTypeDefaultValDefinition {

  def defaultValueDefinition = Set(
      OptionTpe -> None
      ,StringTpe -> ""
      ,IntegerTpe -> 0
      ,BigDecimalTpe -> BigDecimal("0").bigDecimal
      ,DateTpe -> new DateTime("1970-01-01T00:00:00").toDate
      ,OptionNotDataValTpe -> NoneNotDataVal
    )
}
