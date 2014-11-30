package net.shantitree.flow.dbsync.msg.job

import net.shantitree.flow.dbsync.session.JobRunnerRef

case class AnotherJobHasBeenDeferred(runner: JobRunnerRef)
