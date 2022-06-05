/*
rule = SingleConditionMatch
 */
package fix.pixiv

object SingleConditionMatch {

  private val result = Some(1) match {
    case result => result
  }

  Some(1) match {
    case Some(result2) => println(result2)
  }

  Some(1) match {
    case result3 => println(result3)
  }

  Some(1) match {
    case result4 => println(result4.getOrElse(result4))
  }

  Some(1) match {
    case result5 =>
      println(result5)
      println(result5)
  }
}
