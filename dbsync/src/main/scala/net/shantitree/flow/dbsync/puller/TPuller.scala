package net.shantitree.flow.dbsync.puller

/**
 * Created by bkkoo on 15/10/2557.
 */
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.sys.lib.lang.ProductConverter._
import net.shantitree.flow.slick.qry.{BaseQry, View}

trait TPuller[Q <: BaseQry] {

  val view: View[Q]
  val baseQry: Q

  protected def resultToMap(result: List[Product]): List[Map[String, Any]] = {
    result.map(toMap)
  }

  def run()(implicit db: Database): List[Product] = {
    val query = view(baseQry)
    val result = db withSession { implicit s => query.list }
    result
  }

}
