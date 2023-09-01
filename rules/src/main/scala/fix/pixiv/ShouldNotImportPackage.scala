package fix.pixiv

import scala.meta.{Importer, Pkg}

import metaconfig.Configured
import scalafix.Patch
import scalafix.v1.{Configuration, Rule, SemanticDocument, SemanticRule}

class ShouldNotImportPackage(config: LogConfig, shouldNotImportPackageConfig: ShouldNotImportPackageConfig)
    extends SemanticRule("ShouldNotImportPackage") {
  def this() = this(LogConfig(), ShouldNotImportPackageConfig())

  override def withConfiguration(config: Configuration): Configured[Rule] = {
    (config.conf.getOrElse("ShouldNotImportPackage")(this.config) |@| config.conf.getOrElse("ShouldNotImportPackage")(
      this.shouldNotImportPackageConfig
    ))
      .map {
        case (config, config1) => new ShouldNotImportPackage(config, config1)
      }
  }

  private val patterns: Map[String, List[String]] = shouldNotImportPackageConfig.blackList.map { map =>
    val (target, importer) = (for {
      target <- map.get("target")
      importer <- map.get("importer")
    } yield (target, importer))
      .getOrElse(throw new RuntimeException(s"Config の指定が不正です: ${map.mkString(", ")}"))
    importer -> target
  }.foldLeft(Map.empty[String, List[String]]) {
    case (map, (importer, target)) => map + (importer -> List(target))
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // package 宣言を取得
      case Pkg(packageName, _) => packageName.toString()
    } match {
      // 1つだけの場合、 importer にマッチするものを絞り込み
      case List(packageName) =>
        patterns.find {
          case (importer, _) => importer.r.findFirstIn(packageName).nonEmpty
        } match {
          case Some((_, targets)) =>
            doc.tree.collect {
              case i: Importer =>
                val regex = targets.find {
                  _.r.findFirstIn(i.toString()).nonEmpty
                }
                if (regex.isDefined) {
                  Patch.lint(LogLevel(
                    s"${i.toString()} は $packageName から呼び出されるべきではありません",
                    i.pos,
                    config.level
                  ))
                } else Patch.empty
              case _ => Patch.empty
            }.asPatch
          case None => Patch.empty
        }
      case _ => Patch.empty
    }
  }
}
