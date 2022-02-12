# Scalafix rules for Scalafix-Rule-Pixiv

[scalafix](https://scalacenter.github.io/scalafix/) で利用できる汎用的なリファクタリングルール

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