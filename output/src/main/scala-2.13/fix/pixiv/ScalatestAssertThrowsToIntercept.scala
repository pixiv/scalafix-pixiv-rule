package fix.pixiv

import org.scalatest.Assertions
import org.scalatest.Assertions._

object ScalatestAssertThrowsToIntercept {
  Assertions.intercept[RuntimeException] {
    throw new RuntimeException("assertThrows1")
  }
  intercept[RuntimeException] {
    throw new RuntimeException("assertThrows2")
  }
  // 変更なし
  println("assertThrows")
}
