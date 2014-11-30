package net.shantitree.flow.dbsync.session

import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.msg.job.AnotherJobFinished

case class DeferredUntilAnotherFinished(
   runner:JobRunnerRef
  ,session: SyncSession
  ,slog: SyncLog
  ,fn:Option[List[AnotherJobFinished]=>Unit]=None
  ,waitForRunners: Set[JobRunnerRef]=Set()
  ,messages: List[AnotherJobFinished]=List()
) {

  def anotherJobFinished(message: AnotherJobFinished): DeferredUntilAnotherFinished = {
    if (waitForRunners(message.runner)) {
      val accumulatedMessages = message::messages
      val left = waitForRunners.filterNot(_ == message.runner)
      this.copy(waitForRunners = left, messages = accumulatedMessages)
    } else {
      this
    }
  }

  def runWhenReady(): Unit = {
    if (waitForRunners.isEmpty && fn.nonEmpty) {
      fn.get(messages)
    }
  }

  def isWaitFor(runner: JobRunnerRef):Boolean = waitForRunners(runner)

}

