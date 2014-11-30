package net.shantitree.flow.slick.qry.param
import org.joda.time.DateTime

case class CreatedDuration(from: DateTime, until: DateTime) extends Duration
