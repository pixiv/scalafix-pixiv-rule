/*
rule = SingleConditionMatch
 */
package fix.pixiv

object SingleConditionMatch {
  implicit class slash(i: Int) {
    def \(str: String) = ""
  }

  private val result = Some(1) match {
    case result => result
  }

  Some(1) match {
    case Some(result) => println(result)
  }

  Some(1) match {
    case result => println(result)
  }

  Some(1) match {
    case result => println(result.getOrElse(result))
  }

  Some(1) match {
    case result =>
      println(result)
      println(result)
  }

  Some(1) match {
    case Some(result) =>
      result match {
        case result => println(result)
      }
  }

  Some(1) match {
    case result =>
      result match {
        case Some(result2) => println(result2)
        case _ => println("hoge")
      }
  }

  (123 \ "test").length match {
    case i => i.compareTo(2)
  }
}
