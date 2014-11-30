package net.shantitree.flow.dbsync.job

import com.typesafe.config.Config
import org.joda.time.DateTime

import scala.concurrent.duration._

case class SyncJobConfig(runnerName: String, config: Config) {

  lazy val initialDate:DateTime = new DateTime(config.getString("initial_date"))
  lazy val defaultRepeatDelay:FiniteDuration = {
    val path = "default_repeat_delay"
    if (config.hasPath(path)) {
      getDuration(path)
    } else {
      60.seconds
    }
  }
  
  lazy val defaultBulkPartitionByDays: Int = {
    val path = "default_bulk_mode.partition_by_days"
    config.getInt(path)
  }
  
  lazy val defaultBulkWhenNoSyncInDays: Int = {
    val path = "default_bulk_mode.when_no_sync_in_days"
    config.getInt(path)
  }
  
  lazy val repeatDelay:FiniteDuration = {
    val path = s"job.$runnerName.repeat_delay"
    if (config.hasPath(path)) {
      getDuration(path)
    } else {
      defaultRepeatDelay
    }
  }

  lazy val bulkPartitionByDays: Int = {
    try {
      config.getInt(s"job.$runnerName.bulk_mode.partition_by_days")
    } catch {
      case e:Exception =>
        defaultBulkPartitionByDays
    }
  }

  lazy val whenNoSyncInDays:Int = {
    try {
      config.getInt(s"job.$runnerName.bulk_mode.when_no_sync_in_days")
    } catch {
      case e:Exception =>
        defaultBulkWhenNoSyncInDays
    }
  }

  protected def getDuration(path: String):FiniteDuration = {
    val duration = config.getConfig(path)
    val unit = duration.getString("unit")
    val value = duration.getLong("value")
    unit match {
      case "second"   => value.seconds
      case "minute"   => value.minutes
      case "hour"     => value.hours
      case "day"      => value.days
      case _ => throw new IllegalArgumentException(
        s"Invalid unit value '$unit' in config path '$path'! \r\n" +
          s"Must be second, minute, hour or day"
      )
    }
  }

}
