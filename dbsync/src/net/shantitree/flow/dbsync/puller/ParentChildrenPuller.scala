package net.shantitree.flow.dbsync.puller

import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.shantitree.flow.sys.lib.model.{Model, ParentChildrenModel, ParentChildrenModelDef}
import net.shantitree.flow.slick.qry.param._
import net.shantitree.flow.slick.qry.{BaseQry, Condition, View}


class ParentChildrenPuller[PQ <: BaseQry, CQ <: BaseQry, M <: ParentChildrenModel[_<:Model, _<:Model]](
  parentPuller: Puller[PQ],
  childPuller: CQ => Puller[CQ],
  modelDef: ParentChildrenModelDef[M]
)(implicit val childrenFilter: Condition[InCodes, CQ]) extends TPuller[PQ] with GetValueOfMasterKey {

  val masterKey = modelDef.masterKey
  val foreignKey = modelDef.foreignKey
  val view = parentPuller.view
  val baseQry = parentPuller.baseQry

  override def run()(implicit db: Database): List[Product] = {
    resultToMap(parentPuller.run())
      .grouped(50)
      .map { parents =>

        val pairs = parents.map { p => valueOfMasterKey(p) -> p }
        val inCodes = InCodes(pairs.map(_._1))
        val idx = pairs.toMap

        resultToMap(childPuller(inCodes).run())
          .groupBy { child =>
            try {
              child(foreignKey)
            } catch {
              case e:Exception =>
                throw new ForeignKeyNotFoundInChildMapException(
                  s"Can't find foreign key '$foreignKey' in child map (ParentChildrenPuller)"
                )
            }
          }
          .map { case (masterKeyVal, children) =>
            val masterKeyString = try {
              masterKeyVal.asInstanceOf[String]
            } catch {
              case e:ClassCastException=>
                throw new ClassCastException(
                  "Can't convert value of master key retrieved from child map to String (ParentChildrenPuller)")
            }
            idx(masterKeyString) -> children
          }
      }
      .flatten
      .toList
  }
  
}
