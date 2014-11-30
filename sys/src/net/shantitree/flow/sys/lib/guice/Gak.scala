package net.shantitree.flow.sys.lib.guice

import akka.actor._
import com.google.inject.Injector

/**
 * An Akka extension implementation for Guice-based injection. The Extension provides Akka access to
 * dependencies defined in Guice.
 */

class GakImpl extends Extension {

  private var injector: Injector = _

  def initialize(injector: Injector) {
    this.injector = injector
  }

  def props(actorName: String) = Props(classOf[GuiceActorProducer], injector, actorName)

}

object Gak extends ExtensionId[GakImpl] with ExtensionIdProvider {

  /** Register ourselves with then ExtenstionIdProvider */
  override def lookup() = Gak

  /** Called by Akka in order to createModel an instance of the extension. */
  override def createExtension(system: ExtendedActorSystem) = new GakImpl

  /** Java API: Retrieve the extension for the given system. */
  override def get(system: ActorSystem): GakImpl = super.get(system)
}
