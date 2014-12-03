package net.shantitree.flow.sys.lib.actor

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigException.Missing
import net.shantitree.flow.sys.module.NamedActor

object ActorPathConfig {

  protected val actorPathKey = "flow.actorPath"

  protected val actorPathConfig = try {
    ConfigFactory.load().getConfig(actorPathKey)
  } catch {
    case e:Missing =>
      throw new Missing(s"Can't get 'actorPath' setting! key '$actorPathKey' not found in configuration")
    case e:Exception =>
      throw new RuntimeException(s"Can't get 'actorPath' setting! key '$actorPathKey' due to following error '${e.getMessage}'")
  }

  def actorPathOf(id: String): String = {
    try {
      actorPathConfig.getString(id)
    } catch {
      case e:Missing =>
        throw new Missing(s"Can't get actorPath of '$id' setting! key '$actorPathKey.$id' not found in configuration")
      case e:Exception =>
        throw new RuntimeException(s"Can't get actorPath of '$id' setting! key '$actorPathKey.$id' due to following error '${e.getMessage}'")
    }
  }

  def actorPathOf(namedActor: NamedActor): String = {
    actorPathOf(namedActor.actorName)
  }

}
