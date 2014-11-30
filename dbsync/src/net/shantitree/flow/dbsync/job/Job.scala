package net.shantitree.flow.dbsync.job

import net.shantitree.flow.sys.lib.model.TModel
import net.shantitree.flow.slick.qry.param.Param
import net.shantitree.flow.slick.qry.{Condition, BaseQry, View}
import net.shantitree.flow.dbsync.conversion.TModelConverter
import net.shantitree.flow.dbsync.puller.TPuller


trait Job[Q<:BaseQry, M<:TModel] {

  val modelConverter: TModelConverter[M]
  val jobName = this.getClass.getSimpleName.split("\\$").last

  def createPuller(base: Q): TPuller[Q]
  def createPuller[P <: Param](p: P)(implicit condition: Condition[P, Q]):TPuller[Q] = {
    createPuller(condition(p))
  }
  def createUpdater(datum: List[M]): TUpdater

}
