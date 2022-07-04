package util

object ExtendClass {
  implicit class ExtendClass(clazz: Class[_]) {
    def isAssignableTo(sub: Class[_]): Boolean = sub.isAssignableFrom(clazz)
    def isJava: Boolean = {
      import scala.reflect.runtime.universe
      val mirror = universe.runtimeMirror(getClass.getClassLoader)
      mirror.classSymbol(clazz).isJava
    }
    def isScalaPrimitive: Boolean = {
      val primitives = Seq(
        classOf[Int],
        classOf[Double],
        classOf[Float],
        classOf[Byte],
        classOf[Short],
        classOf[Char],
        classOf[Long],
        classOf[Boolean],
        // forName して得られる型は classOf とは別になる
        Class.forName("scala.Int"),
        Class.forName("scala.Double"),
        Class.forName("scala.Float"),
        Class.forName("scala.Byte"),
        Class.forName("scala.Short"),
        Class.forName("scala.Char"),
        Class.forName("scala.Long"),
        Class.forName("scala.Boolean"),
        classOf[String]
      )
      primitives.contains(clazz)
    }
  }
}
