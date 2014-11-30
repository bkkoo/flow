package net.shantitree.flow.sys

import net.shantitree.flow.sys.graph.GraphSession

object InitDb {
  def apply() = {
    initModels()
    initData()
  }

  def initModels() = {
    Partner.init()
    SaleOrderHeader.init()
    SaleOrderItem.init()
    SyncLog.init()
    BzNode.init()
    AcpPosition.init()
    PeriodicAccumulatedPV.init()
    CurAccumPV.init()
    CurAccumPVLog.init()
  }

  def initData() = {
    GraphSession.tx { implicit g =>
      SeedNode.init()
      UnknownPartner.init()
    }
  }

}

