package fix.pixiv

object ZeroIndexToHead {
  val seq = Seq(1, 2, 3)
  seq.head

  Seq(1, 2, 3).head

  val list = List(1, 2, 3)
  list.head

  val indexed = IndexedSeq(1, 2, 3)
  indexed(0)
}
