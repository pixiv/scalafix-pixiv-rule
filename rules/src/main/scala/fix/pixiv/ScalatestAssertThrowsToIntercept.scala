package fix.pixiv

import scalafix.Patch
import scalafix.v1._

import scala.meta._
class ScalatestAssertThrowsToIntercept extends SemanticRule("ScalatestAssertThrowsToIntercept") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Term.ApplyType(name @ Term.Name("assertThrows"), List(_)) if isAssertThrows(name) =>
        Patch.replaceTree(name, "intercept")
      case Term.Select(Term.Name("Assertions"), name @ Term.Name("assertThrows")) if isAssertThrows(name) =>
        Patch.replaceTree(name, "intercept")
      case _ => Patch.empty
    }.asPatch
  }
  private def isAssertThrows(x1: Term)(implicit doc: SemanticDocument): Boolean = {
    x1.symbol == Symbol("org/scalatest/Assertions#assertThrows().")
  }

}
