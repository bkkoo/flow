package net.shantitree.flow.slick.module

import com.google.inject.{Inject, Provides}
import com.google.inject.name.Named
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import com.typesafe.slick.driver.ms.SQLServerDriver.simple._

trait TSqlDbModule { this: ScalaModule=>

  val sqlDbConfigPath: String

  def sqlDbModuleConfigure(): Unit = {
    bind[SqlDbConfig].toProvider[SqlDbConfigProvider].asEagerSingleton()
    bind[Database].toProvider[SqlDbProvider].asEagerSingleton()
  }

  @Provides @Named("SqlDbConfig")
  def getGaiaDbConfig(@Inject() config: Config):Config =  {
    try {
      config.getConfig(sqlDbConfigPath)
    } catch { case e:Exception =>
      throw new RuntimeException(s"Can't get sql db configuration on path '$sqlDbConfigPath'. Following error occur: ${e.getStackTrace}")
    }
  }

}
