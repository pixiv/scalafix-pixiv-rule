/*
rule = CheckIsEmpty
 */
package fix.pixiv

object CheckIsEmpty {
  private val seq = Seq(1, 2, 3)
  private val option = Some(1)

  // to isEmpty
  Seq(1, 2, 3).size == 0
  seq.size == 0
  seq.length == 0
  !seq.nonEmpty

  // to nonEmpty
  seq.size != 0
  seq.size > 0
  seq.size >= 1
  seq.length != 0
  seq.length > 0
  seq.length >= 1
  seq.exists(_ => true)
  seq.exists(Function.const(true))
  seq.size != 0
  seq.length != 0
  !seq.isEmpty

  // to isEmpty
  option.size == 0
  option == None
  None == option
  !option.isDefined

  // to isDefined
  option != None
  None != option
  option.nonEmpty

  // 配列には isEmpty が存在しないので無視する
  Array(1, 2, 3).length == 0
}
