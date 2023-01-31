![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.pixiv/scalafix-pixiv-rule_2.13/badge.png)

# Scalafix rules for Scalafix-Rule-Pixiv

[scalafix](https://scalacenter.github.io/scalafix/) で利用できる汎用的なリファクタリングルール

If you would like the README written in English, please click [here](./README-en.md).

## インストール

```sbt
ThisBuild / scalafixDependencies += "net.pixiv" %% "scalafix-pixiv-rule" % "<VERSIONS>"
```

## fix.pixiv.UnnecessarySemicolon

不要な行末セミコロンを削除します。セミコロン後に行が接続されている場合には削除しません。

```scala
/* rule = UnnecessarySemicolon */
val x = 3; // rewrite to: `val x = 3`
val a = 1; val b = 2
```

## fix.pixiv.UnifiedArrow

非推奨の矢印 (→, ⇒) 文字を ->, => に置換します。

```scala
/* rule = UnifiedArrow */
List(1 → "a", 2 → "b", 3 → "c").map { // rewrite to: List(1 -> "a", 2 -> "b", 3 -> "c").map {
  case (_, s) ⇒ s // rewrite to: case (_, s) => s
}
```

## fix.pixiv.ZeroIndexToHead

`Seq` のインデックスによる最初の要素へのアクセスを `head` 呼び出しに置換します。

```scala
/* rule = ZeroIndexToHead */
Seq(1, 2, 3)(0) // rewrite to: `Seq(1, 2, 3).head`
```

## fix.pixiv.CheckIsEmpty

`Option` や `Seq` の空チェックに `isEmpty`, `nonEmpty`, `isDefined` を利用するように置き換えます。

```scala
/* rule = CheckIsEmpty */
Some(1) == None // rewrite to: Some(1).isEmpty
Some(1).nonEmpty // if `CheckIsEmpty.alignIsDefined = true` then rewrite to Some(1).isDefined
```

## fix.pixiv.NonCaseException

`Exception` を継承した `case class` の定義に警告を発生させます。
これは、 `case class` として実装することにより、例外の階層化や一意性による恩恵が損なわれるためです。

```scala
/* rule = NonCaseException */
case class NonCaseException(msg: String) extends RuntimeException(msg)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
case class として Exception を継承することは推奨されません: NonCaseException
```

## fix.pixiv.UnifyEmptyList

⚠️ Scala 2.13.x 系のみ対応しています。

型変数指定のない `List()` や `List.empty` を `Nil` に置き換えます。
これは、 `Nil` が `List[Nothing]` として定義されているためです。
また、型変数指定のある `List[Any]()` は `List.empty[Any]` へと置換されます。

```scala
/* rule = EmptyListToNil */
val empty = List.empty // rewrite to: val empty = Nil
val list = List[String]() // rewrite to: val list = List.empty[String]
```

## fix.pixiv.SingleConditionMatch

単一の `case` しか持たないパターンマッチを分解します。

また、 `Some.unapply` のみが利用されているパターンを `foreach` 呼び出しに置換します。

<b>※このルールではインデントが破壊されることがあります。必ずフォーマッターと併用してください</b>

```scala
/* rule = SingleConditionMatch */
Some(1) match {
  case result => println(result)
}

/* rewrite to: 
 * println(Some(1))
 */
```

技術的な問題により、ネストされたパターンでは、最深のものだけが置換されます。

```scala
/* rule = SingleConditionMatch */
Some(1) match {
  case result =>
    result match {
      case result => println(result)
    }
}

/* rewrite to: 
 * Some(1) match {
 *   case result =>
 *     println(result)
 * }
 */
```

## fix.pixiv.MockitoThenToDo

[Mockito](https://site.mockito.org/) を Scala で使う場合の記法を統一します。

when/then 構文は戻り値型のチェックを受けられる反面で、 void (Unit) を返却したい場合は使えないなどのデメリットがあります。
このルールでは、 when/then 構文で書かれたテストコードを do/when 構文に置き換えます。

```scala
/* rule = MockitoThenToDo */
Mockito.when(a.hoge()).thenReturn("mock1").thenReturn("mock2")
when(a.fuga).thenReturn("mock")

/* rewrite to:
 * Mockito.doReturn("mock1").doReturn("mock2").when(a).hoge()
 * Mockito.doReturn("mock").when(a).fuga
 */
```

## fix.pixiv.ScalatestAssertThrowsToIntercept
[Scalatest](https://www.scalatest.org/)のExceptionのassertionを統一します。

[using_assertions](https://www.scalatest.org/user_guide/using_assertions)のドキュメントで、Exceptionsのassertionの場合には`assertThrows`と`intercept`がありますが、 二つの違いは`intercept`はExceptionsを値として返し、`assertThrows`は返さないという振る舞いをします。
このルールでは`assertThrows`を`intercept`に変更するものです。
```scala
/* rule = ScalatestAssertThrowsToIntercept */
assertThrows[RuntimeException]{
  a.hoge()
}

/* rewrite to:
 * intercept[RuntimeException]{
 *   a.hoge()
 * }
 */
```
