package fix.pixiv

import scala.meta.tokens.Token

import scalafix.Patch
import scalafix.v1.{SyntacticDocument, SyntacticRule}

class UnifiedArrow extends SyntacticRule("UnifiedArrow") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tokens.map {
      case t @ Token.Ident("→") => Patch.replaceToken(t, "->")
      case t: Token.RightArrow => Patch.replaceToken(t, "=>")
      case _ => Patch.empty
    }.asPatch
  }
}
