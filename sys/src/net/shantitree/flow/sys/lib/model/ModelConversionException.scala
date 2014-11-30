package net.shantitree.flow.sys.lib.model

case class ModelConversionException(message: String, cause: Throwable)
  extends RuntimeException(message) {
  if (cause != null) initCause(cause)
  def this(message: String) = this(message, null)
}
