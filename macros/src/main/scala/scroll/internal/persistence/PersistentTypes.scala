package scroll.internal.persistence

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

class PersistentTypes(entities: String*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro PersistentTypesImpl.impl
}

object PersistentTypesImpl {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    def extractAnnotationParameters(tree: Tree): List[c.universe.Tree] = tree match {
      case q"new $name( ..$params )" => params
      case _ => throw new Exception("PersistentTypes annotation must be have at least one parameter.")
    }

    val sensitiveFields = extractAnnotationParameters(c.prefix.tree)

    def extractNewToString(sensitiveFields: List[Tree]): List[c.universe.Tree] = {
      val cases = sensitiveFields map {
        case f =>
          val n = f.toString.replace("\"", "")
          val newName = TermName("create" + n)
          CaseDef(Literal(Constant(n)), EmptyTree, q"$newName(playerID, db)")
      }
      val m = Match(q"playerType", cases :+ CaseDef(Ident(termNames.WILDCARD), EmptyTree, q"""throw new RuntimeException("Match error: '" + playerType + "'")"""))
      val factoryMethod =
        q"""
          override def create(playerID: Long, playerType: String, db: Instance): Any = $m
        """
      val creators = sensitiveFields.map {
        case f =>
          val n = f.toString.replace("\"", "")
          val typeName = TypeName(n)
          val newName = TermName("create" + n)
          q"def $newName(playerID: Long, db: Instance):$typeName = db.fetchById[$typeName](playerID).mixoutPersisted[$typeName]._2"
      }
      creators :+ factoryMethod
    }

    def modifiedDeclaration(classDecl: ClassDef): c.Expr[Any] = {
      val newToString = extractNewToString(sensitiveFields)
      c.Expr[Any](
        q"""
          import sorm.Instance
          class Factory extends TypeFactory {
            ..$newToString
          }""")
    }

    annottees.map(_.tree).toList match {
      case (classDecl: ClassDef) :: Nil => modifiedDeclaration(classDecl)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}