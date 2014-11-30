package net.shantitree.flow.sys.lib.orient.graph

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert
import com.tinkerpop.blueprints.impls.orient.{OrientBaseGraph => BaseGraph, OrientGraph => Tx, OrientGraphNoTx => NoTx}

import scala.concurrent.{ExecutionContext, Future}

trait TGraphSession {

  val factoryProvider:TGraphFactoryProvider

  lazy val factory = factoryProvider.get()
  lazy val poolMinSize = factoryProvider.poolMinSize
  lazy val poolMaxSize = factoryProvider.poolMaxSize

  protected def _session[G <: BaseGraph, R](graph: G, onErr: G=>Unit)(fn: G => R): R = {
    try {
      val result = fn(graph)
      result
    } catch {
      case e: Throwable =>
        onErr(graph)
        throw e
    } finally {
      graph.shutdown()
    }

  }

  def futureTx[R](fn: Tx=>R)(implicit ec: ExecutionContext):Future[R] = Future { _session(factory.getTx, (g:Tx)=>g.rollback())(fn) }
  def tx[R](fn: Tx=>R):R = _session(factory.getTx, (g:Tx)=>g.rollback())(fn)
  def futureNoTx[R](fn: NoTx=>R)(implicit ec:ExecutionContext):Future[R] = Future { _session(factory.getNoTx, (g:NoTx)=>Unit)(fn) }
  def noTx[R](fn: NoTx=>R):R = _session(factory.getNoTx, (g:NoTx)=>Unit)(fn)
  def txWithMassInst[R](fn: Tx=>R):R = _session(factory.getTx, (g:Tx)=>g.rollback()){ g =>
    g.getRawGraph.declareIntent(new OIntentMassiveInsert)
    val result = fn(g)
    g.getRawGraph.declareIntent(null)
    result
  }
  def noTxWithMassInst[R](fn: NoTx=>R):R = _session(factory.getNoTx, (g:NoTx)=>Unit) {g =>
    g.getRawGraph.declareIntent(new OIntentMassiveInsert)
    val result = fn(g)
    g.getRawGraph.declareIntent(null)
    result
  }
}
