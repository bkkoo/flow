package net.shantitree.flow.sys

import net.shantitree.flow.sys.lib.model.TVertexWrapperBase

trait PVGetter[T] {
  def getPV(obj: T): PV
}
object PVGetter {

  implicit object PurePVGetter extends PVGetter[PV] {
    def getPV(pv:PV) = pv
  }

  implicit object VertexPVGetter extends PVGetter[Vertex] {
    def getPV(v: Vertex) = PVVertexWrapper(v).getPV
  }

  implicit object OrientVertexPVGetter extends PVGetter[OrientVertex] {
    def getPV(v: OrientVertex) = PVVertexWrapper(v).getPV
  }

  implicit object Vertex_PV_PVGetter extends PVGetter[(Vertex, PV)] {
    def getPV(p: (Vertex, PV)) = p._2
  }

}

object PV {

  def sum[T](tv: TraversableOnce[T])(implicit pvGetter: PVGetter[T]): PV = {
    PV().sum(tv)
  }

  def groupAndSum[K, T](it: Traversable[(K, T)])(implicit pvGetter: PVGetter[T]): Map[K, PV] = {
    it.groupBy(_._1)
      .map { case (k, myIt) =>
        val itT = myIt.map(_._2)
        (k, PV().sum(itT))
      }
  }

  def groupByAndSum[K, T, G](it: Traversable[(K, T)])(fn:((K, T)) => G)(implicit pvGetter: PVGetter[T]): Map[G, PV] = {
    it.groupBy(fn)
      .map { case (k, myIt) =>
      val itT = myIt.map(_._2)
      (k, PV().sum(itT))
    }
  }
}

case class PVVertexWrapper(v: Vertex) extends TVertexWrapperBase {

  def getPV = PV(pv("ppv"), pv("qpv"), pv("cpv"))

  protected def pv(name: String): Int = {
    val value = getInt(name)
    if (value == null) { 0 } else { value }
  }

}

case class PV(ppv: Int=0, qpv: Int=0, cpv: Int=0) {

  def ++(pv: PV): PV = {
    PV(ppv + pv.ppv,  qpv + pv.qpv, cpv + pv.cpv)
  }

  def +++[T](t: T)(implicit pvGetter: PVGetter[T]):PV = {
    ++(pvGetter.getPV(t))
  }

  def **(factor: Int):PV = {
    PV(ppv*factor, qpv*factor, cpv*factor)
  }

  def toReturnPVIf(isReturn: Boolean):PV = {
    if (isReturn) { **(-1) } else { this }
  }

  def sum[T](tv: TraversableOnce[T])(implicit pvGetter: PVGetter[T]): PV = {
    tv.foldLeft(this) { (b, a) => b +++ a }
  }

}


