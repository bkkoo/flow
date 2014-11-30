package net.shantitree.flow.sys.gen

import java.io.FileWriter

import com.orientechnologies.orient.core.metadata.schema.{OType, OClass}
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory
import scala.collection.JavaConverters._

trait TGenModelFieldName {

  val factory:OrientGraphFactory
  val mainPackage: String
  val packagePath: String
  val fileName: String

  val imports: List[String] = List()
  val importsInMainPackage: List[String] = List()

  val genOClasses: Set[String]

  def apply() = {
    val dir = s"src/main/scala/${mainPackage.split('.').mkString("/")}/${packagePath.split('.').mkString("/")}"
    val fileWriter = new FileWriter(s"$dir/$fileName.scala")
    fileWriter.write(genStatement())
    fileWriter.close()
  }

  final protected def genObjectStatements(oClasses: List[OClass]): String = {
    oClasses
      .filter { c => genOClasses(c.getName) }
      .map { c =>
        val className = c.getName
        val items = c.declaredProperties.asScala.map { p=> (p.getName, p.getType) }

        objectStatement(className, items)
      }
      .mkString("")
  }

  final protected def genItemClauses(items: Iterable[(String, OType)]) = {
    items.map { p => itemClause(p._1, p._2) }.mkString("\r\n  ")
  }

  protected def objectStatementHeader(className: String) = s"object ${className}Field"
  protected def itemClause(name: String, otype: OType) = s"""val $name = "$name""""
  protected def objectStatementFooter = ""

  final protected def objectStatement(className: String, items: Iterable[(String, OType)]):String = {
    s"""
       |${objectStatementHeader(className)} {
       |  ${genItemClauses(items)}
       |  $objectStatementFooter
       |}
     """.stripMargin
  }

  protected def genSuperType() = ""
  protected def genStatement() = {

    val schema = factory.getDatabase.getMetadata.getSchema
    val objectStatements = genObjectStatements(schema.getClasses.asScala.toList) + "\r\n"
    val superType = genSuperType()

    s"""
         |package $mainPackage.$packagePath
         |
         |${imports.map {i=> s"import $i"}.mkString("\r\n")}
         |${importsInMainPackage.map {i=> s"import $mainPackage.$i"}.mkString("\r\n")}
         |
         |$objectStatements
         |
         |$superType
         |
      """.stripMargin

  }

}
