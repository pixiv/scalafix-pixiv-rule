package fix.pixiv

import scala.meta._

import scalafix.Patch
import scalafix.v1._
class ScalatestAssertThrowsToIntercept extends SemanticRule("ScalatestAssertThrowsToIntercept") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ q"assertThrows" =>
        Patch.replaceTree(t, "intercept")
      case _ => Patch.empty
    }.asPatch
  }

}
