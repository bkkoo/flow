package net.shantitree.flow.sys.lib.orient.graph

import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import net.shantitree.flow.sys.lib.module.GetConfig

trait TGraphFactoryProvider extends GetConfig {

  protected val graphConfigPath: String

  protected lazy val graphConfig =  try {
    config.getConfig(graphConfigPath)
  } catch { case e:Exception =>
    throw new RuntimeException(s"Can't get graph configuration on path '$graphConfigPath'. Following error occur: ${e.getStackTrace}")
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
