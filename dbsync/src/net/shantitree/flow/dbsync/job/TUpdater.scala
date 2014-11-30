package net.shantitree.flow.dbsync.job

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.dbsync.msg.result.{TSyncResult, NoneResult}

trait TUpdater {
  val update: OrientGraph => Any

  def run()(implicit g: OrientGraph): TSyncResult = {
    update(g) match {
      case r:TSyncResult => r
      case r => NoneResult
    }
  }

}
