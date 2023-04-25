package fix.pixiv

import scala.meta.{Defn, Init, Mod, Template}

import metaconfig.Configured
import scalafix.Patch
import scalafix.v1.{Configuration, Rule, SemanticDocument, SemanticRule, XtensionTreeScalafix}
import util.SymbolConverter.SymbolToSemanticType

class NonCaseException(config: LogConfig) extends SemanticRule("NonCaseException") {
  def this() = this(LogConfig())

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("NonCaseException")(this.config)
      .map { newConfig => new NonCaseException(newConfig) }
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Defn.Class(List(_: Mod.Case), name, _, _, _ @Template(_, List(_ @Init(typ, _, _)), _, _)) =>
        try {
          if (typ.symbol.isAssignableTo(classOf[Exception])) {
            Patch.lint(LogLevel(s"case class として Exception を継承することは推奨されません: $name", t.pos, config.level))
          } else {
            Patch.empty
          }
        } catch {
          case _: Throwable => Patch.empty
        }
    }.asPatch
  }
}
