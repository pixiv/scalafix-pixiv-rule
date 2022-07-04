/*
rule = UnnecessaryParenForJava
 */
package fix.pixiv

import java.text.NumberFormat

object UnnecessaryParenForJava {
  val i = 123
  i.toString()
  println(123.toString())
  NumberFormat.getNumberInstance()
  new RuntimeException().getStackTrace().mkString("Array(", ",", ")")
}
