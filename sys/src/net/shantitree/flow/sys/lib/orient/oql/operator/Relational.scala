package net.shantitree.flow.sys.lib.orient.oql.operator

import java.util.Date

import net.shantitree.flow.sys.lib.orient.oql.Criteria
import net.shantitree.flow.sys.lib.lang.DateTimeHelper._
import net.shantitree.flow.sys.lib.orient.oql.param.DateTimeParam
import org.joda.time.DateTime

object Relational {

  implicit def stringToRelational(property: String): Relational = new Relational(property)

  implicit object StringOprVal extends OperandVal[String] {
    def getValue(op: String, value: String ) = s"'$value'"
  }

  implicit object IntOprVal extends OperandVal[Int] {
    def getValue(op: String, value: Int) = value.toString
  }

  implicit object IntegerOprVal extends OperandVal[Integer] {
    def getValue(op: String, value: Integer) = value.toString
  }

  implicit object LongOprVal extends OperandVal[Long] {
    def getValue(op: String, value: Long) = value.toString
  }

  implicit object DoubleOprVal extends OperandVal[Double] {
    def getValue(op: String, value: Double) = value.toString
  }

  implicit object FloatOprVal extends OperandVal[Float] {
    def getValue(op: String, value: Float) = value.toString
  }

  implicit object DateOprVal extends OperandVal[Date] {
    def getValue(op: String, value: Date) = s"'${DateTimeParam.mkParam(value.toJoda)}'"
  }

  implicit object JodaDateTimeOprVal extends OperandVal[DateTime] {
    def getValue(op: String, value: DateTime) = s"'${DateTimeParam.mkParam(value)}'"
  }

  implicit object DateTimeParamOprVal extends OperandVal[DateTimeParam] {
    def getValue(op: String, value: DateTimeParam) = s"'${value.param}'"
  }

}

class Relational(property: String) {

  def `===`[T](value: T)(implicit rightOperand: OperandVal[T]) = toCriteria("=", value)
  def `>>`[T](value: T)(implicit rightOperand: OperandVal[T]) = toCriteria(">", value)
  def `>>=`[T](value: T)(implicit rightOperand: OperandVal[T]) = toCriteria(">=", value)
  def `<<`[T](value: T)(implicit rightOperand: OperandVal[T]) = toCriteria("<", value)
  def `<<=`[T](value: T)(implicit rightOperand: OperandVal[T]) = toCriteria("<=", value)

  def toCriteria[T](op: String, value: T)(implicit operand: OperandVal[T]) = {
    new Criteria { val clause = s"$property $op ${operand.getValue(op, value)}" }
  }
}

trait OperandVal[T] {
  def getValue(op: String, value: T): String
}
