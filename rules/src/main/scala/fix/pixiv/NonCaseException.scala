package fix.pixiv

import scala.meta.{Defn, Init, Mod, Position, Template}

import scalafix.Patch
import scalafix.lint.{Diagnostic, LintSeverity}
import scalafix.v1.{SemanticDocument, SemanticRule, XtensionTreeScalafix}
import util.SymbolConverter.SymbolToSemanticType

class NonCaseException extends SemanticRule("NonCaseException") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Class(List(_: Mod.Case), name, _, _, _ @Template(_, List(_ @Init(typ, _, _)), _, _)) =>
        try {
          if (typ.symbol.isAssignableTo(classOf[Exception])) {
            Patch.lint(NonCaseExceptionWarn(s"case class として Exception を継承することは推奨されません: $name", t.pos))
          } else {
            Patch.empty
          }
        } catch {
          case _: Throwable => Patch.empty
        }
    }.asPatch
  }
}

case class NonCaseExceptionWarn(override val message: String, position: Position) extends Diagnostic {
  override def severity: LintSeverity = LintSeverity.Warning
}
