package net.shantitree.flow.experiment

/*
import net.shantitree.flow.model._
import net.shantitree.flow.sys.graph.GraphSession
import net.shantitree.flow.sys.lib.orient.graph.GraphHelper._
import net.shantitree.flow.sys.lib.lang.Box
import net.shantitree.flow.model.constraint.EdgeLabelConst

object ExperimentB {
  val so = SaleOrderHeaderField
  val si = SaleOrderItemField
  val bn = BzNodeField
  val E = EdgeLabelConst

  def apply() = {
    import com.tinkerpop.gremlin.scala.ScalaVertex._

    GraphSession.tx { implicit g =>
      val result =
        g.vSaleOrders[SaleOrderHeader](so.com_period -> Box(20080101))
        .map { v =>
          val rs2 = v
            .out(E.Item)

            //.groupBy()(v=>v.getProperty[String](si.order_code), v=>v.getProperty[Int](si.ppv))
            .gather
            .map(_.foldLeft((0,0,0)){ (sum, v) =>
              val vw = SaleOrderItemVW(v)
              (vw.ppv + sum._1, vw.qpv + sum._2, vw.cpv + sum._3)
            })
            .cap
            .next()
        }


      println(result)

    }
  }
}
*/
