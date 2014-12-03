package net.shantitree.flow.slick

case class SqlDbConfig(
  jdbcDriver: String,
  slickDriver: String,
  dbUser: String,
  dbPassword: String,
  url: String
)
