package net.shantitree.flow.sys.lib.orient.graph

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import com.typesafe.config.ConfigFactory

trait TGraphFactoryProvider {

  protected val dbConfigPath: String
  protected lazy val config = ConfigFactory.load()

  protected lazy val graphConfig =  try {
    config.getConfig(dbConfigPath)
  } catch { case e:Exception =>
    throw new RuntimeException(s"Can't get graph configuration on path '$dbConfigPath'. Following error occur: ${e.getStackTrace}")
  }

  protected lazy val url = graphConfig.getString("url")
  protected lazy val pool = graphConfig.getConfig("pool")
  lazy val List(poolMinSize, poolMaxSize) = {
    try {
      List("poolMinSize","poolMaxSize") map pool.getInt
    } catch {
      case e: Exception =>
        List(5, 10)
    }
  }

  def get() = new OrientGraphFactory(url).setupPool(poolMinSize, poolMaxSize)

}
