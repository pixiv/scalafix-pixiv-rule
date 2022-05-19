# Scalafix rules for Scalafix-Rule-Pixiv

[scalafix](https://scalacenter.github.io/scalafix/) で利用できる汎用的なリファクタリングルール
Generic refactoring rules available in [scalafix](https://scalacenter.github.io/scalafix/) 

## インストール (Install)

```sbt
ThisBuild / scalafixDependencies += "net.pixiv" %% "scalafix-pixiv-rule" % "<VERSIONS>"
```

## fix.pixiv.UnnecessarySemicolon

不要な行末セミコロンを削除します。セミコロン後に行が接続されている場合には削除しません。
Deletes unneeded end-of-line semicolons. If a line is connected after the semicolon, it is not deleted.

```scala
/* rule = UnnecessarySemicolon */
val x = 3; // rewrite to: `val x = 3`
val a = 1; val b = 2
```

## fix.pixiv.ZeroIndexToHead

`Seq` のインデックスによる最初の要素へのアクセスを `head` 呼び出しに置換します。
Replaces access to the first element by index in `Seq` with a call to `head`.

```scala
/* rule = ZeroIndexToHead */
Seq(1, 2, 3)(0) // rewrite to: `Seq(1, 2, 3).head`
```

## fix.pixiv.CheckIsEmpty

`Option` や `Seq` の空チェックに `isEmpty`, `nonEmpty`, `isDefined` を利用するように置き換えます。
Replace `Option` and `Seq` emptiness checks with `isEmpty`, `nonEmpty`, and `isDefined`.

```scala
/* rule = CheckIsEmpty */
Some(1) == None // rewrite to: Some(1).isEmpty
Some(1).nonEmpty // if `CheckIsEmpty.alignIsDefined = true` then rewrite to Some(1).isDefined
```

## fix.pixiv.NonCaseException

`Exception` を継承した `case class` の定義に警告を発生させます。
Raise a warning for `case class` definitions that inherit from `Exception`.

これは、 `case class` として実装することにより、例外の階層化や一意性による恩恵が損なわれるためです。
The reason this is necessary is that the benefits of exception hierarchy and identity are lost when implemented as a `case class'.

```scala
/* rule = NonCaseException */
case class NonCaseException(msg: String) extends RuntimeException(msg)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
case class として Exception を継承することは推奨されません: NonCaseException
```

## fix.pixiv.UnifyEmptyList

型変数指定のない `List()` や `List.empty` を `Nil` に置き換えます。
Replace `List()` and `List.empty` without type variables with `Nil`.

これは、 `Nil` が `List[Nothing]` として定義されているためです。
This is because `Nil` is defined as `List[Nothing]`.

また、型変数指定のある `List[Any]()` は `List.empty[Any]` へと置換されます。
Also, for consistency, `List[Any]()`, which had a type variable specification, has been replaced with `List.empty[Any]`.

```scala
/* rule = EmptyListToNil */
val empty = List.empty // rewrite to: val empty = Nil
val list = List[String]() // rewrite to: val list = List.empty[String]
```