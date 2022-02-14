package util

import scalafix.v1.{ClassSignature, MethodSignature, SemanticType, Symtab, TypeRef, TypeSignature, ValueSignature}
import util.SemanticTypeConverter.SemanticTypeToClass

object SymbolConverter {
  implicit class SymbolToSemanticType(symbol: scalafix.v1.Symbol)(implicit doc: Symtab) {
    def toSemanticType: SemanticType = symbol.info.fold(
      throw new ToClassException(s"${symbol.displayName} は info を持ちません。")
    ) { info =>
      info.signature match {
        case MethodSignature(_, _, returnType) =>
          returnType
        case ValueSignature(tpe) =>
          tpe
        case TypeSignature(_, lowerBound, upperBound) =>
          if (lowerBound == upperBound) {
            lowerBound
          } else {
            throw new RuntimeException(
              s"型パラメータ ${symbol.displayName} の型は一意に定まりません。 lower: $lowerBound, upper: $upperBound"
            )
          }
        case ClassSignature(typeParameters, _, _, _) =>
          TypeRef(scalafix.v1.NoType, symbol, typeParameters.map(info => info.symbol.toSemanticType))
        case signature =>
          throw new ToClassException(s"${symbol.displayName} (${signature.getClass}) は型を持ちません")
      }
    }

    def isAssignableFrom(clazz: Class[_]): Boolean = symbol.toSemanticType.isAssignableFrom(clazz)

    def isAssignableTo(clazz: Class[_]): Boolean = symbol.toSemanticType.isAssignableTo(clazz)

  }

}
