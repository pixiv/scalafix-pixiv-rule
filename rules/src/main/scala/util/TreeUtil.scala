package util

import scala.meta.Tree

object TreeUtil {
  implicit class ShallowCollectTree(tree: Tree) {
    def shallowCollect[T](fn: PartialFunction[Tree, Option[T]]): List[T] = {
      def traverse(tree: Tree): List[T] = fn(tree) match {
        case None => tree.children.flatMap(traverse)
        case Some(value) => List(value)
      }
      traverse(tree)
    }
  }
}
