package net.shantitree.flow.dbsync.puller

trait GetValueOfMasterKey {
  val masterKey: String
  def valueOfMasterKey(masterRow: Map[String, Any]):String = {
    masterRow.get(masterKey) match {
      case Some(id) =>
        try {
          id.asInstanceOf[String]
        } catch {
          case e: ClassCastException =>
            throw new ClassCastException("Can't convert value of master key to String!")
          case e: Exception =>
            throw e
        }
      case None =>
        throw new NoSuchElementException(s"Value of master key '$masterKey' is not defined")
    }
  }
}
