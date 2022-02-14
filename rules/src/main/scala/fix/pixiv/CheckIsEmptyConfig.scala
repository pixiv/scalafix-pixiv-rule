package fix.pixiv

import metaconfig.ConfDecoder
import metaconfig.generic.Surface

final case class CheckIsEmptyConfig(alignIsDefined: Boolean = false)

object CheckIsEmptyConfig {
  val default: CheckIsEmptyConfig = CheckIsEmptyConfig()
  implicit val surface: Surface[CheckIsEmptyConfig] = metaconfig.generic.deriveSurface[CheckIsEmptyConfig]
  implicit val decoder: ConfDecoder[CheckIsEmptyConfig] = metaconfig.generic.deriveDecoder(default)
}
