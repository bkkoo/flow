package net.shantitree.flow.sys.graph

import net.shantitree.flow.sys.lib.orient.graph.TGraphSession

object GraphSession extends TGraphSession {
  lazy val factoryProvider = GraphFactoryProvider
}
