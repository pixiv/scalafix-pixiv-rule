/*
rule = ZeroIndexToHead
 */
package fix.pixiv

object ZeroIndexToHead {
  val seq = Seq(1, 2, 3)
  seq(0)

  Seq(0) // これは apply 相当のため .head に変換されない

  Seq(1, 2, 3)(0)

  val list = List(1, 2, 3)
  list(0)

  val indexed = IndexedSeq(1, 2, 3)
  indexed(0)

  Some(Seq(1, 2, 3)).get(0) // このパターンではまだ型の取得が実装できていないので変換されない
}
