/*
rule = UnifyEmptyList
 */
package fix.pixiv

object UnifyEmptyList {
  // 変換する
  private val list = List()
  private val nothingList = List[Nothing]()
  private val empty = List.empty
  private val varType: List[String] = List()
  List(1, 2).foldLeft[List[Int]](List.empty)((l, a) => l.appended(a))
  varType match {
    case List() => 0
    case "1" :: "2" :: Nil => 2
    case _ => -1
  }
  private def func(list: List[String] = List()): Unit = {}
  // empty に変換する
  private val listType = List[String]()
  List(1, 2).foldLeft(List[Int]())((l, a) => l.appended(a))
  private def typeFunc(list: List[String] = List[String]()): Unit = {}
  // 変換しない
  private val emptyType = List.empty[String]
  List(1, 2).foldLeft(List.empty[Int])((l, a) => l.appended(a))
}
