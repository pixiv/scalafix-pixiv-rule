package fix.pixiv

object UnifyEmptyList {
  // 変換する
  private val list = Nil
  private val nothingList = Nil
  private val empty = Nil
  private val varType: List[String] = Nil
  List(1, 2).foldLeft[List[Int]](Nil)((l, a) => l :+ a)
  varType match {
    case Nil => 0
    case "1" :: "2" :: Nil => 2
    case _ => -1
  }
  private def func(list: List[String] = Nil): Unit = {}
  // empty に変換する
  private val listType = List.empty[String]
  List(1, 2).foldLeft(List.empty[Int])((l, a) => l :+ a)
  private def typeFunc(list: List[String] = List.empty[String]): Unit = {}
  // 変換しない
  private val emptyType = List.empty[String]
  List(1, 2).foldLeft(List.empty[Int])((l, a) => l :+ a)
}
