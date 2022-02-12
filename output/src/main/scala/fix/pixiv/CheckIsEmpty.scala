/*
rule = CheckIsEmpty
 */
package fix.pixiv

object CheckIsEmpty {
  private val seq = Seq(1, 2, 3)
  private val option = Some(1)

  // to isEmpty
  Seq(1, 2, 3).isEmpty
  seq.isEmpty
  seq.isEmpty
  seq.isEmpty

  // to nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty
  seq.nonEmpty

  // to isEmpty
  option.isEmpty
  option.isEmpty
  option.isEmpty
  option.isEmpty

  // to isDefined
  option.isDefined
  option.isDefined
  option.isDefined

  // 配列には isEmpty が存在しないので無視する
  Array(1, 2, 3).length == 0
}
