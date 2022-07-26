![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.pixiv/scalafix-pixiv-rule_2.13/badge.png)

# Scalafix rules for Scalafix-Rule-Pixiv

General refactoring rules available in [scalafix](https://scalacenter.github.io/scalafix/)

日本語版の README.md は [こちら](./README.md)

## Install

```sbt
ThisBuild / scalafixDependencies += "net.pixiv" %% "scalafix-pixiv-rule" % "<VERSIONS>"
```

## fix.pixiv.UnnecessarySemicolon

Remove unnecessary end-of-line semicolons. If a line is connected after the semicolon, it will be not deleted.

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

Replace emptiness checks for `Option` and `Seq` with `isEmpty`, `nonEmpty`, and `isDefined`.

```scala
/* rule = CheckIsEmpty */
Some(1) == None // rewrite to: Some(1).isEmpty
Some(1).nonEmpty // if `CheckIsEmpty.alignIsDefined = true` then rewrite to Some(1).isDefined
```

## fix.pixiv.NonCaseException

Raise a warning for `case class` definitions that inherit from `Exception`.

This is because implementing `case class` would compromise the benefits of exception hierarchy and uniqueness.

```scala
/* rule = NonCaseException */
case class NonCaseException(msg: String) extends RuntimeException(msg)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
case class として Exception を継承することは推奨されません: NonCaseException
```

## fix.pixiv.UnifyEmptyList

Replace `List()` and `List.empty` without type variables specification with `Nil`.

This is because `Nil` is defined as `List[Nothing]`.

Also, `List[Any]()` with a type variable specification is replaced with `List.empty[Any]`.

```scala
/* rule = EmptyListToNil */
val empty = List.empty // rewrite to: val empty = Nil
val list = List[String]() // rewrite to: val list = List.empty[String]
```

## fix.pixiv.SingleConditionMatch

Split a pattern match that has only one `case`.

It also replaces patterns where only `Some.unapply` is used with `foreach` calls.

<b>⚠︎This rule may destroy indentation. Always use with a formatter!</b>

```scala
/* rule = SingleConditionMatch */
Some(1) match {
  case result => println(result)
}

/* rewrite to:
 * println(Some(1))
 */
```

Due to technical issues, only the most deeply nested patterns will be replaced.

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
