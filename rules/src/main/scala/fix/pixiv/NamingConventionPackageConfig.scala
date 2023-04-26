package fix.pixiv
import metaconfig.ConfDecoder
import metaconfig.generic.Surface

case class NamingConventionPackageConfig(
    convention: List[Map[String, String]] = List.empty
)

object NamingConventionPackageConfig {
  val default: NamingConventionPackageConfig = NamingConventionPackageConfig()
  implicit val surface: Surface[NamingConventionPackageConfig] =
    metaconfig.generic.deriveSurface[NamingConventionPackageConfig]
  implicit val decoder: ConfDecoder[NamingConventionPackageConfig] = metaconfig.generic.deriveDecoder(default)
}
