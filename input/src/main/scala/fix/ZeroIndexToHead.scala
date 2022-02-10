/*
rule = ZeroIndexToHead
 */
package fix

object ZeroIndexToHead {
  val seq = Seq(1, 2, 3)
  seq(0)

  Seq(1, 2, 3)(0)

  val list = List(1, 2, 3)
  list(0)

  val indexed = IndexedSeq(1, 2, 3)
  indexed(0)
}
