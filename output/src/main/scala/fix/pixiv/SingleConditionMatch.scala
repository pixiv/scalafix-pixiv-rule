package fix.pixiv

object SingleConditionMatch {

  private val result = Some(1)

  val result2 = 1

  println(Some(1))

  {
  val result4 = Some(1)
  println(result4.getOrElse(result4))
}
}
