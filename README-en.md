# Scalafix rules for Scalafix-Rule-Pixiv

Generic refactoring rules available in [scalafix](https://scalacenter.github.io/scalafix/)

日本語版の README.md は [こちら](./README.md)

## Install

```sbt
ThisBuild / scalafixDependencies += "net.pixiv" %% "scalafix-pixiv-rule" % "<VERSIONS>"
```

## fix.pixiv.UnnecessarySemicolon

Deletes unneeded end-of-line semicolons. If a line is connected after the semicolon, it is not deleted.

```scala
/* rule = UnnecessarySemicolon */
val x = 3; // rewrite to: `val x = 3`
val a = 1; val b = 2
```

## fix.pixiv.ZeroIndexToHead

Replaces access to the first element by index in `Seq` with a call to `head`.

```scala
/* rule = ZeroIndexToHead */
Seq(1, 2, 3)(0) // rewrite to: `Seq(1, 2, 3).head`
```

## fix.pixiv.CheckIsEmpty

Replace `Option` and `Seq` emptiness checks with `isEmpty`, `nonEmpty`, and `isDefined`.

```scala
/* rule = CheckIsEmpty */
Some(1) == None // rewrite to: Some(1).isEmpty
Some(1).nonEmpty // if `CheckIsEmpty.alignIsDefined = true` then rewrite to Some(1).isDefined
```

## fix.pixiv.NonCaseException

Raise a warning for `case class` definitions that inherit from `Exception`.

The reason this is necessary is that the benefits of exception hierarchy and identity are lost when implemented as a `case class'.

```scala
/* rule = NonCaseException */
case class NonCaseException(msg: String) extends RuntimeException(msg)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
case class として Exception を継承することは推奨されません: NonCaseException
```

## fix.pixiv.UnifyEmptyList

Replace `List()` and `List.empty` without type variables with `Nil`.

This is because `Nil` is defined as `List[Nothing]`.

Also, for consistency, `List[Any]()`, which had a type variable specification, has been replaced with `List.empty[Any]`.

```scala
/* rule = EmptyListToNil */
val empty = List.empty // rewrite to: val empty = Nil
val list = List[String]() // rewrite to: val list = List.empty[String]
```

## fix.pixiv.SingleConditionMatch
Split a pattern match that has only one `case`.

It also rewrites a pattern that uses only `Some.unapply` for pattern matching into a statement that calls `foreach`.

<b>⚠︎This rule can destroy indentation. Be sure to use it with a formatter.</b>

```scala
/* rule = SingleConditionMatch */
Some(1) match {
  case result => println(result)
}

/* rewrite to: 
 * println(Some(1))
 */
```

Due to technical issues, only the most deeply nested patterns are substituted.

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