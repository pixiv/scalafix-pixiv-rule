package util

import scala.collection.Seq

import org.scalatest.funsuite.AnyFunSuite
import scalafix.v1.{SingleType, TypeRef}
import util.SemanticTypeConverter.SemanticTypeToClass

class SemanticTypeConverterTest extends AnyFunSuite {

  test("SemanticTypeToClass: TypeRef") {
    assert(
      classOf[Seq[_]] == TypeRef(null, scalafix.v1.Symbol("scala/collection/Seq#"), null).toClass
    )
  }

  test("SemanticTypeToClass: SingleType") {
    assert(classOf[Seq[_]] == SingleType(null, scalafix.v1.Symbol("scala/collection/Seq#")).toClass)
  }

  test("isAssignableFrom") {
    assert(SingleType(null, scalafix.v1.Symbol("scala/collection/Seq#")).isAssignableFrom(classOf[List[_]]))
  }

  test("isAssignableTo") {
    assert(SingleType(null, scalafix.v1.Symbol("scala/collection/Seq#")).isAssignableFrom(classOf[Seq[_]]))
  }

  test("symbolToClass: クラス名を取得できる") {
    assert(classOf[Seq[_]] == SemanticTypeConverter.symbolToClass(scalafix.v1.Symbol("scala/collection/Seq#")))
  }

  test("symbolToClass: 型パラメータを持つクラス名を取得できる") {
    assert(
      classOf[List[_]] == SemanticTypeConverter.symbolToClass(scalafix.v1.Symbol("scala/collection/immutable/List#[T]"))
    )
  }

  test("symbolStringToClass: クラス名を取得できる") {
    assert(classOf[Seq[_]] == SemanticTypeConverter.symbolStringToClass("scala.collection.Seq"))
  }

  test("symbolStringToClass: type で再定義された型からも取得できる") {
    assert(classOf[java.lang.String] == SemanticTypeConverter.symbolStringToClass("scala.Predef.String"))
  }

}
