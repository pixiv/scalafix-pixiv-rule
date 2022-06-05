package fix.pixiv

object SingleConditionMatch {

  private val result = Some(1)

  Some(1).foreach {
  result2 => println(result2)
}

  println(Some(1))

  {
  val result4 = Some(1)
  println(result4.getOrElse(result4))
}

  {
  val result5 = Some(1)
  println(result5)
  println(result5)
}
}