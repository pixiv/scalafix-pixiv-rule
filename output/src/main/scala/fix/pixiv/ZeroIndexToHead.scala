package fix.pixiv

object ZeroIndexToHead {
  val seq = Seq(1, 2, 3)
  seq.head

  Seq(0) // これは apply 相当のため .head に変換されない

  Seq(1, 2, 3).head

  val list = List(1, 2, 3)
  list.head

  val indexed = IndexedSeq(1, 2, 3)
  indexed(0)

  Some(Seq(1, 2, 3)).get(0) // このパターンではまだ型の取得が実装できていないので変換されない
}
