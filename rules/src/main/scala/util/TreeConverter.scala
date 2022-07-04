package util

import scala.meta.Lit

object TreeConverter {
  implicit class LitToClass(literal: Lit) {
    @throws[ClassCastException]
    def toClass: Class[_] = literal match {
      case Lit.Int(_) => classOf[Int]
      case Lit.Double(_) => classOf[Double]
      case Lit.Float(_) => classOf[Float]
      case Lit.Byte(_) => classOf[Byte]
      case Lit.Short(_) => classOf[Short]
      case Lit.Char(_) => classOf[Char]
      case Lit.Long(_) => classOf[Long]
      case Lit.Boolean(_) => classOf[Boolean]
      case Lit.String(_) => classOf[String]
      case Lit.Null() => classOf[Null]
      case Lit.Unit() => classOf[Unit]
      case _ => throw new ClassCastException()
    }
  }
}
