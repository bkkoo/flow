package net.shantitree.flow.sys.lib.orient.oql

import net.shantitree.flow.sys.lib.orient.oql.OqlParamUtil.odate
import org.joda.time.DateTime

/**
 * Created by bkkoo on 28/11/2557.
 */
case class Between(`this`: AnyRef*)(that: AnyRef*) extends OqlParam {

  override def toString = {

    require(`this`.length == that.length)

    val this_that = `this`.zip(that) map {
      case (x:DateTime, y: DateTime) => (odate(x), odate(y))
      case (x:DateTime, None) =>
        val dt = odate(x)
        (dt, dt)
      case (x:DateTime, y) => (odate(x),y. toString)
      case (x, y: DateTime) => (x.toString, odate(y))
      case (x, None) => (x.toString, x.toString)
      case (x, y) => (x.toString, y.toString)
    }

    val (this2, that2) = this_that.unzip
    val this_and_that = (List(this2,that2) map {
      case x::Nil => x
      case xs => s"[${xs.mkString(",")}]"
    }).mkString(" and ")

    s"between $this_and_that"

  }
}
