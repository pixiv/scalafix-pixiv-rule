# Scalafix rules for Scalafix-Rule-Pixiv

## fix.pixiv.UnnecessarySemicolon

不要な行末セミコロンを削除します。セミコロン後に行が接続されている場合には削除しません。

```scala
val x = 3; // rewrite to: `val x = 3`
val a = 1; val b = 2
```