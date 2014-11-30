package net.shantitree.flow.dbsync.puller

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.sys.lib.lang.ProductConverter._
import net.shantitree.flow.slick.qry.param.InCodes

trait SubPullers {
  protected def runPullers(inCodes: InCodes)(implicit db: Database): List[(String, List[Product])]
  def run(inCodes: InCodes)(implicit db: Database): List[(String, List[Map[String, Any]])] = {
    runPullers(inCodes).map { case (name, pRows) =>  name -> pRows.map(_.toMap) }
  }
}
