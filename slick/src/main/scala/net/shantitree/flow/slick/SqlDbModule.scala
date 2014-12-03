package net.shantitree.flow.slick

import com.google.inject.{Inject, Provides, Singleton}
import com.typesafe.config.ConfigFactory
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._
import net.codingwell.scalaguice.ScalaModule

trait SqlDbModule { this: ScalaModule=>

  val sqlDbConfigPath: String

  @Provides
  def sqlDbConfigProvider():SqlDbConfig = {
    val config = try {
      ConfigFactory.load().getConfig(sqlDbConfigPath)
    } catch { case e:Exception =>
      throw new RuntimeException(s"Can't get sql db configuration on path '$sqlDbConfigPath'. Following error occur: ${e.getStackTrace}")
    }
    SqlDbConfig(
      jdbcDriver = config.getString("jdbc-driver"),
      slickDriver = config.getString("slick-driver"),
      dbUser = config.getString("user"),
      dbPassword = config.getString("password"),
      url = config.getString("url")
    )

  }

  @Provides @Singleton
  def sqlDbProvider(@Inject() config: SqlDbConfig):Database = {
    Class.forName(config.jdbcDriver)
    Database.forURL(
      config.url,
      driver=config.slickDriver,
      user=config.dbUser,
      password=config.dbPassword
    )
  }

}
