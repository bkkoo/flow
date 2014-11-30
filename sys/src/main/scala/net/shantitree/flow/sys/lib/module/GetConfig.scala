package net.shantitree.flow.sys.lib.module

import com.typesafe.config.ConfigFactory

trait GetConfig {
  def config = ConfigFactory.load()
}
