package fix.pixiv

object UnifiedArrow {
  List(1 -> "a", 2 -> "b", 3 -> "c").map {
    case (_, s) => s
  }
  for {
    a <- List(1, 2, 3)
  } yield a
}
