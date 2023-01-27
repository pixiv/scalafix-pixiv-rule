/*
rule = ScalatestAssertThrowsToIntercept
 */
package fix.pixiv

import org.scalatest.Assertions
import org.scalatest.Assertions._

object ScalatestAssertThrowsToIntercept {
  Assertions.assertThrows[RuntimeException] {
    throw new RuntimeException("assertThrows1")
  }
  assertThrows[RuntimeException] {
    throw new RuntimeException("assertThrows2")
  }
  // 変更なし
  println("assertThrows")
}
