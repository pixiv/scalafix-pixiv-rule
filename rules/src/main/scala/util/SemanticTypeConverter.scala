package util

import scala.util.Try

import scalafix.v1.{BooleanConstant, ByteConstant, CharConstant, Constant, ConstantType, DoubleConstant, FloatConstant, IntConstant, LongConstant, NullConstant, SemanticType, ShortConstant, SingleType, StringConstant, SuperType, ThisType, TypeRef, UnitConstant}

object SemanticTypeConverter {
  implicit class SemanticTypeToClass(semanticType: SemanticType) {
    @throws[ToClassException]
    def toClass: Class[_] = {
      semanticType match {
        case TypeRef(_, symbol, _) =>
          symbolToClass(symbol)
        case SingleType(_, symbol) =>
          symbolToClass(symbol)
        case ConstantType(constant) =>
          constantToClass(constant)
        case SuperType(_, symbol) =>
          symbolToClass(symbol)
        case ThisType(symbol) =>
          symbolToClass(symbol)
        case _ =>
          throw new ToClassException(s"${semanticType.toClass} の Class[_] への変換は定義されていません。")
      }
    }

    def isAssignableFrom(clazz: Class[_]): Boolean = semanticType.toClass.isAssignableFrom(clazz)

    def isAssignableTo(clazz: Class[_]): Boolean = clazz.isAssignableFrom(semanticType.toClass)

  }

  @throws[ToClassException]
  def symbolToClass(symbol: scalafix.v1.Symbol): Class[_] = {
    val identifierRegStr = "[a-zA-Z$_][a-zA-Z1-9$_]*"
    val symbolRegStr = s"($identifierRegStr(?:[/.]$identifierRegStr)*)"
    val finRegStr = "[#.]"
    val typeRegStr = s"\\[$identifierRegStr(?:,$identifierRegStr)*]$$"
    val typeValMatch = (s"^$symbolRegStr$finRegStr(?:$typeRegStr)?$$").r
    val funcValMatch = (s"^$symbolRegStr$finRegStr($identifierRegStr)\\(\\)$finRegStr(?:$typeRegStr)?$$").r
    symbol.toString() match {
      case typeValMatch(str1) => symbolStringToClass(str1.replace('/', '.'))
      case funcValMatch(str1, str2) =>
        try {
          val holder = symbolStringToClass(str1.replace('/', '.'))
          // メソッドの返り値をリフレクションで取得する
          import scala.reflect.runtime.universe.{TermName, runtimeMirror}
          val mirror = runtimeMirror(getClass.getClassLoader)
          mirror.runtimeClass(
            mirror.classSymbol(holder).info.member(TermName(str2)).asMethod.returnType
          )
        } catch {
          // メソッドかつ戻り値が T の時は型が取得できない
          // 外側のメソッドあたりから頑張れば取れるかもしれない
          case e: Throwable =>
            throw new ToClassException(s"$str1.$str2() を完全修飾クラス名に変換できませんでした: ${e.getMessage}(${e.getClass})")
        }
      case _ => throw new ToClassException(s"${symbol.toString()} を完全修飾クラス名に変換できませんでした")
    }
  }

  @throws[ToClassException]
  def symbolStringToClass(str: String): Class[_] = {
    Try {
      // 本来はオブジェクトなら '$' をつけるべきだが、`apply` の戻り値は元のクラスのため、そちらに合わせる
      Class.forName(str)
    }.recover[Class[_]] {
      case _ =>
        // package object 内で type として再代入されている場合には上記の方法では取得できないため、
        // 一度コンパニオンオブジェクトを取得してからフィールドとして取り出している。
        val lastDot = str.lastIndexOf('.')
        val objCls = Class.forName(str.take(lastDot) + "$")
        import scala.reflect.runtime.universe.{TypeName, runtimeMirror}
        val mirror = runtimeMirror(getClass.getClassLoader)
        val clazz: Class[_] = mirror.runtimeClass(mirror.classSymbol(objCls).toType.decl(
          TypeName(str.drop(lastDot + 1))
        ).typeSignature.dealias.typeSymbol.asClass)
        clazz
    }.recover[Class[_]] {
      case _: ClassNotFoundException =>
        throw new ToClassException(s"$str をクラスまたはタイプに変換できませんでした")
      case _ => throw new ToClassException("オブジェクトから type を取得できませんでした")
    }.get
  }

  def constantToClass(constant: Constant): Class[_] = constant match {
    case UnitConstant => classOf[Unit]
    case BooleanConstant(_) => classOf[Boolean]
    case ByteConstant(_) => classOf[Byte]
    case ShortConstant(_) => classOf[Short]
    case CharConstant(_) => classOf[Char]
    case IntConstant(_) => classOf[Int]
    case LongConstant(_) => classOf[Long]
    case FloatConstant(_) => classOf[Float]
    case DoubleConstant(_) => classOf[Double]
    case StringConstant(_) => classOf[String]
    case NullConstant => classOf[Null]
  }
}
