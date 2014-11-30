package net.shantitree.flow.dbsync.msg.job

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import net.shantitree.flow.dbsync.model.SyncLog
import net.shantitree.flow.dbsync.session.JobRunnerRef

case class ExecInGraphSession(runner: JobRunnerRef, slog: SyncLog)(val fn: OrientGraph=>Any)
