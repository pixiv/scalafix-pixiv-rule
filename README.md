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

また、 `Some.unapply` のみが利用されているパターンを `foreach` 呼び出しに書き換えます。

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