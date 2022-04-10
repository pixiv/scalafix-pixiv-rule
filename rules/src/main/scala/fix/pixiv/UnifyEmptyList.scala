package fix.pixiv

import scala.meta._

import scalafix.v1._
import util.SymbolConverter.SymbolToSemanticType

class UnifyEmptyList extends SemanticRule("UnifyEmptyList") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Term.Apply(Term.ApplyType(list @ Term.Name("List"), List(Type.Name(typeVar))), Nil) if isList(list) =>
        typeVar match {
          // List[Nothing]()
          case "Nothing" => Patch.replaceTree(t, "Nil")
          // List[Any]()
          case _ => Patch.replaceTree(
              t,
              Term.ApplyType(Term.Select(Term.Name("List"), Term.Name("empty")), List(Type.Name(typeVar))).toString()
            )
        }
      // List()
      case t @ Term.Apply(list @ Term.Name("List"), Nil) if isList(list) =>
        Patch.replaceTree(t, "Nil")
      // List.empty[Nothing]
      case t @ Term.ApplyType(Term.Select(list @ Term.Name("List"), Term.Name("empty")), List(Type.Name("Nothing")))
          if isList(list) =>
        Patch.replaceTree(t, "Nil")
      case t @ Term.Select(list @ Term.Name("List"), Term.Name("empty")) if isList(list) =>
        t.parent match {
          // List.empty[Nothing]
          case Some(Term.ApplyType(_, List(Type.Name("Nothing")))) => Patch.replaceTree(t, "Nil")
          // List.empty[String] は変換しない
          case Some(_: Term.ApplyType) => Patch.empty
          // List.empty
          case _ => Patch.replaceTree(t, "Nil")
        }
      case t @ Pat.Extract(list @ Term.Name("List"), Nil) if isList(list) =>
        Patch.replaceTree(t, "Nil")
    }.asPatch
  }

  private def isList(x1: Term)(implicit doc: SemanticDocument): Boolean = {
    try {
      x1.symbol.isAssignableTo(classOf[collection.immutable.List[Any]])
    } catch {
      case _: Throwable => false
    }
  }

}
