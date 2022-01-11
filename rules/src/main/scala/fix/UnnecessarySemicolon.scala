package fix

import scala.meta.tokens.Token

import scalafix.Patch
import scalafix.v1.{SyntacticDocument, SyntacticRule}

class UnnecessarySemicolon extends SyntacticRule("UnnecessarySemicolon") {

  override def fix(implicit doc: SyntacticDocument): Patch = {
    doc.tokens.zipWithIndex.collect {
      case (semicolon: Token.Semicolon, i) =>
        doc.tokens(i+1) match {
          case _@ (Token.CR() | Token.LF() | Token.EOF()) => Patch.replaceToken(semicolon, "")
          case _ => Patch.empty
        }
    }.asPatch
  }
}
