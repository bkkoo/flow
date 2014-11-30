package net.shantitree.flow.sys.lib.module

import com.google.inject.{AbstractModule, Provider}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import net.shantitree.flow.sys.lib.module.GetConfig


object ConfigModule {
  class ConfigProvider extends Provider[Config] with GetConfig {
    override def get() = config
  }
}

/**
 * Binds the application configuration to the [[com.typesafe.config.Config]] interface.
 *
 * The config is bound as an eager singleton so that errors in the config are detected
 * as early as possible.
 */

class ConfigModule extends AbstractModule with ScalaModule {
  import net.shantitree.flow.sys.lib.module.ConfigModule._

  override def configure() {
    bind[Config].toProvider[ConfigProvider].asEagerSingleton()
  }

}

