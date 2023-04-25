package fix.pixiv

import scala.meta.Position

import scalafix.lint.{Diagnostic, LintSeverity}

case class LogLevel(override val message: String, override val position: Position, level: String) extends Diagnostic {
  override def severity: LintSeverity = level match {
    case "Info" => LintSeverity.Info
    case "Warn" => LintSeverity.Warning
    case "Error" => LintSeverity.Error
    // 設定に異常値が入っている場合はデフォルトとして扱う
    case _ => LintSeverity.Warning
  }
}
