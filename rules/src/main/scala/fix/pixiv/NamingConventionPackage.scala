package fix.pixiv

import scala.meta.{Defn, Pkg}
import scala.util.matching.Regex

import metaconfig.Configured
import scalafix.Patch
import scalafix.v1.{Configuration, Rule, SemanticDocument, SemanticRule}

class NamingConventionPackage(config: LogConfig, namingConventionPackageConfig: NamingConventionPackageConfig)
    extends SemanticRule("NamingConventionPackage") {
  def this() = this(LogConfig(), NamingConventionPackageConfig())

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    (config.conf.getOrElse("NamingConventionPackage")(this.config) |@| config.conf.getOrElse("NamingConventionPackage")(
      this.namingConventionPackageConfig
    ))
      .map {
        case (config, config1) => new NamingConventionPackage(config, config1)
      }
  }

  private val patterns: List[(Regex, Regex)] = namingConventionPackageConfig.convention.map { map =>
    val (pkg, cls) = (for {
      pkg <- map.get("package")
      cls <- map.get("class")
    } yield (pkg, cls))
      .getOrElse(throw new RuntimeException(s"Config の指定が不正です: ${map.mkString(", ")}"))
    pkg.r -> cls.r
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case Pkg(packageName, list) =>
        list.map {
          case t @ Defn.Class(_, className, _, _, _) =>
            patterns.find {
              case (pkg, cls) =>
                pkg.findFirstIn(packageName.toString()).nonEmpty && cls.findFirstIn(className.value).nonEmpty
            }.fold(Patch.empty) { _: (Regex, Regex) =>
              Patch.lint(LogLevel(
                s"class ${className.value} は ${packageName.toString()} パッケージに実装すべきではありません",
                t.pos,
                config.level
              ))
            }
          case _ => Patch.empty
        }.asPatch
    }.asPatch
  }

}
