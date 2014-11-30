package net.shantitree.flow.sys.lib.model

case class ModelReflectionException (message: String, cause: Throwable)
  extends RuntimeException(message) {
  if (cause != null) initCause(cause)
  def this(message: String) = this(message, null)
}
