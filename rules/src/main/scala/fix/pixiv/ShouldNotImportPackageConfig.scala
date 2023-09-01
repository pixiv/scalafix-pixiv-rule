package fix.pixiv

import metaconfig.ConfDecoder
import metaconfig.generic.Surface

case class ShouldNotImportPackageConfig(
    blackList: List[Map[String, String]] = List.empty
)

object ShouldNotImportPackageConfig {
  val default: ShouldNotImportPackageConfig = ShouldNotImportPackageConfig()
  implicit val surface: Surface[ShouldNotImportPackageConfig] =
    metaconfig.generic.deriveSurface[ShouldNotImportPackageConfig]
  implicit val decoder: ConfDecoder[ShouldNotImportPackageConfig] = metaconfig.generic.deriveDecoder(default)
}
