package net.shantitree.flow.sys.gen

import com.orientechnologies.orient.core.metadata.schema.OType


trait TGenVertexWrapper extends TGenModelFieldName{

  override protected def itemClause(name: String, otype: OType) = {
    val typeStr = otype.getDefaultJavaType.getName
    s"""
    |  def $name:$typeStr = v.getProperty[$typeStr]("$name")
    |  def set_$name(value: $typeStr):Boolean = set[$typeStr]("$name", value)""".stripMargin
  }

  override protected def objectStatementHeader(className: String) = s"case class ${className}VW(v: Vertex) extends TVertexWrapper[$className]"
  override protected def genSuperType =
    s"""
     """.stripMargin

}
