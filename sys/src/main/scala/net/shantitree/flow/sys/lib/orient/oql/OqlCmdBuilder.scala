package net.shantitree.flow.sys.lib.orient.oql

import com.orientechnologies.orient.core.sql.OCommandSQL
import net.shantitree.flow.sys.lib.model.Model
import net.shantitree.flow.sys.lib.orient.oql.operator.BetweenThisAndThat$


object OqlCmdBuilder {
  
  implicit def indexBetween[M <: Model] = new OqlCmdBuilder[Index[M], Between] {
    
    def toOCommandSQL(idx: Index[M], between: Between) = {
      new OCommandSQL(s"select from $idx where key $between")
    }
    
  }

}

trait OqlCmdBuilder[A <: AnyRef, P <: OqlParam] {
  
  def toOCommandSQL(a: A, param: P): OCommandSQL
  
}
