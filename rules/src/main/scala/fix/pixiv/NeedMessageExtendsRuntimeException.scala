package fix.pixiv

import scala.meta.{Defn, Init, Position, Template}

import metaconfig.Configured
import scalafix.Patch
import scalafix.lint.{Diagnostic, LintSeverity}
import scalafix.v1.{Configuration, Rule, SemanticDocument, SemanticRule, XtensionTreeScalafix}
import util.SemanticTypeConverter.SemanticTypeToClass
import util.SymbolConverter.SymbolToSemanticType

class NeedMessageExtendsRuntimeException(config: LogConfig) extends SemanticRule("NeedMessageExtendsRuntimeException") {
  def this() = this(LogConfig())

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("NeedMessageExtendsRuntimeException")(this.config)
      .map { newConfig => new NeedMessageExtendsRuntimeException(newConfig) }
  }

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
            Patch.lint(LogLevel(
              s"RuntimeException を継承したクラスを作る際にはメッセージを付与してください: $name",
              t.pos,
              config.level
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
