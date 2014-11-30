package net.shantitree.flow.dbsync.puller

case class ForeignKeyNotFoundInChildMapException (message: String, cause: Throwable)
  extends RuntimeException(message) {
  if (cause != null) initCause(cause)
  def this(message: String) = this(message, null)
}
