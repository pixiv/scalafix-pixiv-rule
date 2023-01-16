/*
rule = ScalatestAssertThrowsToIntercept
 */
package fix.pixiv

import org.scalatest.Assertions

object ScalatestAssertThrowsToIntercept {
  Assertions.intercept[RuntimeException] {
    throw new RuntimeException("assertThrows")
  }
  //変更なし
  println("assertThrows")
}
