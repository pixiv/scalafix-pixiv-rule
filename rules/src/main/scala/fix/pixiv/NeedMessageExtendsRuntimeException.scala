package fix.pixiv

import scala.meta.{Defn, Init, Position, Template}

import scalafix.Patch
import scalafix.lint.{Diagnostic, LintSeverity}
import scalafix.v1.{SemanticDocument, SemanticRule, XtensionTreeScalafix}
import util.SymbolConverter.SymbolToSemanticType
import util.SemanticTypeConverter.SemanticTypeToClass

class NeedMessageExtendsRuntimeException extends SemanticRule("NeedMessageExtendsRuntimeException") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Class(
            _,
            name,
            _,
            _,
            _ @Template(_, List(_ @Init(typ, _, msg)), _, _)
          ) =>
        try {
          if (typ.symbol.toSemanticType.toClass == classOf[RuntimeException] && msg.flatten.isEmpty) {
            Patch.lint(NeedMessageExtendsRuntimeExceptionError(
              s"RuntimeException を継承したクラスを作る際にはメッセージを付与してください: $name",
              t.pos
            ))
          } else {
            Patch.empty
          }
        } catch {
          case _: Throwable => Patch.empty
        }
    }.asPatch
  }
}

case class NeedMessageExtendsRuntimeExceptionError(override val message: String, position: Position)
    extends Diagnostic {
  override def severity: LintSeverity = LintSeverity.Error
}
