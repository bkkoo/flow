package net.shantitree.flow.sys.lib.lang

import net.shantitree.flow.sys.lib.model.conversion.OptionNotDataVal
import org.joda.time.DateTime
import scala.reflect.runtime.universe._
import java.util.{Date => JavaDate}
import java.math.{BigDecimal=> JavaBigDecimal}

object RuTypeDefinition {

  /** ----- AnyRef ------------------*/
  val StringTpe = typeOf[String]
  val IntegerTpe = typeOf[Integer]
  val BigDecimalTpe = typeOf[JavaBigDecimal]
  val OptionTpe = typeOf[Option[Any]]
  val DateTpe = typeOf[JavaDate]
  val DateTimeTpe = typeOf[DateTime]
  val OptionNotDataValTpe = typeOf[OptionNotDataVal[Any]]
  
  /** ----- Application Specific  ------------------*/

}
