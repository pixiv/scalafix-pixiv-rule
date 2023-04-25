package fix.pixiv

import metaconfig.ConfDecoder
import metaconfig.generic.Surface

final case class LogConfig(
    level: String = "Warn"
)

object LogConfig {
  val default: LogConfig = LogConfig()
  implicit val surface: Surface[LogConfig] = metaconfig.generic.deriveSurface[LogConfig]
  implicit val decoder: ConfDecoder[LogConfig] = metaconfig.generic.deriveDecoder(default)
}
