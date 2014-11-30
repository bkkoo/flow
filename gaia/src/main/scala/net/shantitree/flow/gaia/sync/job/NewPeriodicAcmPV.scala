package net.shantitree.flow.gaia.sync.job

import net.shantitree.flow.base.bznet.sys.PeriodicAccumPVDML
import net.shantitree.flow.base.bznet.model.{AccumPVField, AccumPV}
import net.shantitree.flow.gaia.qry.base.PeriodicAccumulatedPVBase
import net.shantitree.flow.gaia.qry.view.PeriodicAccumulatedPVView
import net.shantitree.flow.sys.lib.model.ModelConversionException
import net.shantitree.flow.dbsync.conversion.{DataNormalizer, SMConverter}
import net.shantitree.flow.dbsync.job.{Updater, Job}
import net.shantitree.flow.dbsync.puller.Puller

object NewPeriodicAcmPV
  extends Job[PeriodicAccumulatedPVBase, AccumPV] {

  val f = AccumPVField

  def createPuller(baseQry: PeriodicAccumulatedPVBase) = Puller(baseQry, PeriodicAccumulatedPVView)

  val modelConverter = SMConverter(AccumPV, DataNormalizer{ mRow => Map(
    f.cpv -> 0.asInstanceOf[Long]
    ,f.year -> {
      try {
        mRow(f.year).toString.toInt
      } catch {
        case e:ClassCastException =>
          throw new ModelConversionException(s"Invalid valid year value '${mRow(f.year)}'", e)
        case e:Exception =>
          throw e
      }
    }
    ,f.month -> {
      try {
        mRow(f.month).toString.toInt
      } catch {
        case e:ClassCastException =>
          throw new ModelConversionException(s"Invalid valid month value '${mRow(f.year)}'", e)
        case e:Exception =>
          throw e
      }
    }
  )})

  def createUpdater(datum: List[AccumPV]) = {

    val groupedDatum = datum
      .groupBy(_.member_code)
      .map { case (memberCode, models) =>
        memberCode -> models.groupBy(_.year)
      }

    Updater { implicit g =>
      groupedDatum.foreach { case (memberCode, yearlyMap) =>
        yearlyMap.foreach { case (year, models) =>
          PeriodicAccumPVDML.addNew(memberCode, year, models)
        }
      }
    }
  }

}
