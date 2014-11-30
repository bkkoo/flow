package net.shantitree.flow.slick.module

case class SqlDbConfig(
  jdbcDriver: String,
  slickDriver: String,
  dbUser: String,
  dbPassword: String,
  url: String
)
