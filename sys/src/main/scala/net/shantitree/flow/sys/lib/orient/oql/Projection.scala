package net.shantitree.flow.sys.lib.orient.oql

object Projection {

  object `@Rid` extends Projection {
    lazy val str = "@rid"
  }

  case class Max(field: String) extends Projection {
    lazy val str = s"max($field)"
  }

  protected case class As(projection: Projection, field: String) extends Projection {
    lazy val str = s"${projection.str} as $field"
  }

}

trait Projection {
  import Projection._
  val str:String
  def as(field: String) = As(this, field)
}
