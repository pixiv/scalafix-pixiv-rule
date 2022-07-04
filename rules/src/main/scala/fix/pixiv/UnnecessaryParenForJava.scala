package fix.pixiv

import java.lang.reflect.Modifier

import scala.meta._

import scalafix.v1._
import util.ExtendClass.ExtendClass
import util.SemanticTypeConverter.SemanticTypeToClass
import util.SymbolConverter.SymbolToSemanticType
import util.ToClassException
import util.TreeConverter.LitToClass

class UnnecessaryParenForJava extends SemanticRule("UnnecessaryParenForJava") {
  override def fix(implicit doc: SemanticDocument): Patch = doc.tree.collect {
    case t @ Term.Apply(Term.Select(origin, method: Term.Name), Nil) if nonStaticJava(origin, method) =>
      Patch.replaceTree(
        t,
        Term.Select(origin, method).toString
      )
  }.asPatch

  def nonStaticJava(origin: Term, method: Term.Name)(implicit doc: SemanticDocument): Boolean =
    try {
      val originClass = {
        try {
          origin.symbol.toSemanticType.toClass
        } catch {
          case e: ToClassException =>
            origin match {
              case literal: Lit => literal.toClass
              case _ => throw e
            }
        }
      }

      originClass.isJava && !Modifier.isStatic(originClass.getMethod(method.value).getModifiers) || (
        (originClass.isScalaPrimitive || originClass.isPrimitive) && method.value == "toString"
      )
    } catch {
      case _: Throwable => false
    }
}
