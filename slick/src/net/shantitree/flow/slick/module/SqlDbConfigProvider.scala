package net.shantitree.flow.slick.module

import com.google.inject.{Provider, Inject}
import com.google.inject.name.Named
import com.typesafe.config.Config

class SqlDbConfigProvider @Inject() (@Named("SqlDbConfig") val config:Config ) extends Provider[SqlDbConfig] {
  override def get() = SqlDbConfig(
    jdbcDriver = config.getString("jdbc-driver"),
    slickDriver = config.getString("slick-driver"),
    dbUser = config.getString("user"),
    dbPassword = config.getString("password"),
    url = config.getString("url")
  )
}
