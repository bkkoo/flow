package net.shantitree.flow.slick.module

import com.google.inject.{Provider, Inject}
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._

class SqlDbProvider @Inject() (val config:SqlDbConfig) extends Provider[Database] {
  override def get() = {
    // Register JDBC Driver.
    Class.forName(config.jdbcDriver)
    Database.forURL(
      config.url,
      driver=config.slickDriver,
      user=config.dbUser,
      password=config.dbPassword
    )
  }
}
