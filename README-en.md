![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.pixiv/scalafix-pixiv-rule_2.13/badge.svg)

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

## fix.pixiv.UnifiedArrow

Replace deprecated arrow (→, ⇒) characters with ->, =>.

```scala
/* rule = UnifiedArrow */
List(1 → "a", 2 → "b", 3 → "c").map { // rewrite to: List(1 -> "a", 2 -> "b", 3 -> "c").map {
  case (_, s) ⇒ s // rewrite to: case (_, s) => s
}
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

⚠️ Only Scala 2.13.x is supported

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


## fix.pixiv.MockitoThenToDo

Unify the notation for using [Mockito](https://site.mockito.org/) in Scala.

While when/then syntax can be used to check the return type, it has some disadvantages, such as not being able to return void (Unit).
This rule replaces test code written in when/then syntax with do/when syntax.

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
Unify the assertion of Exceptions for using [Scalatest](https://www.scalatest.org/).

In the case of Exception assertion in article [using_assertions](https://www.scalatest.org/user_guide/using_assertions), there are `assertThrows` and `intercept` that difference between `assertThrows` and `intercept` is `intercept` returns Exceptions but `assertThrows` does not.
Applying this rule changes `assertThrows` to `intercept`.
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
