package net.shantitree.flow.sys

import net.shantitree.flow.sys.lib.orient.graph.{TGraphFactoryProvider, TGraphSession}

object GraphSession extends TGraphSession {
  lazy val factoryProvider = new TGraphFactoryProvider {
    val dbConfigPath = "flow.db"
  }
}
