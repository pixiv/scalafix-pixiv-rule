package fix.pixiv

import scala.collection.compat.IterableOnce
import scala.meta.{Lit, Term, Tree}

import fix.pixiv.CheckIsEmpty.isType
import metaconfig.Configured
import scalafix.v1.{Configuration, Patch, Rule, SemanticDocument, SemanticRule, XtensionTreeScalafix}
import util.SymbolConverter.SymbolToSemanticType

class CheckIsEmpty(config: CheckIsEmptyConfig) extends SemanticRule("CheckIsEmpty") {
  def this() = this(CheckIsEmptyConfig.default)
  override def withConfiguration(config: Configuration): Configured[Rule] = {
    config.conf.getOrElse("CheckIsEmpty")(this.config)
      .map { newConfig => new CheckIsEmpty(newConfig) }
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    implicit val alignIsDefined: Boolean = config.alignIsDefined
    doc.tree.collect {
      case t @ IsDefined(x1, rewrite) if rewrite && isType(x1, classOf[Option[Any]]) =>
        Patch.replaceTree(t, Term.Select(x1, Term.Name("isDefined")).toString())
      case t @ NonEmpty(x1, rewrite) if rewrite && CheckIsEmpty.isTypeHasIsEmpty(x1) =>
        Patch.replaceTree(t, Term.Select(x1, Term.Name("nonEmpty")).toString())
      case t @ IsEmpty(x1, rewrite) if rewrite && CheckIsEmpty.isTypeHasIsEmpty(x1) =>
        Patch.replaceTree(t, Term.Select(x1, Term.Name("isEmpty")).toString())
    }.asPatch
  }
}

private object CheckIsEmpty {
  def isTypeHasIsEmpty(x1: Term)(implicit doc: SemanticDocument): Boolean = {
    isType(x1, classOf[IterableOnce[Any]]) || isType(x1, classOf[Option[Any]])
  }

  def isType(x1: Term, clazz: Class[_])(implicit doc: SemanticDocument): Boolean = {
    x1 match {
      case x1: Term.Name => x1.symbol.isAssignableTo(clazz)
      case x1 @ Term.Apply(_: Term.Name, _) => x1.symbol.isAssignableTo(clazz)
      case _ @Term.Select(_, x1: Term.Name) => x1.symbol.isAssignableTo(clazz)
      case _ @Term.Apply(_ @Term.Select(_, x1: Term.Name), _) => x1.symbol.isAssignableTo(clazz)
      case _ => false
    }
  }
}

private object IsEmpty {
  def unapply(tree: Tree)(implicit doc: SemanticDocument): Option[(Term, Boolean)] = {
    implicit val alignIsDefined: Boolean = true
    tree match {
      // `seq.isEmpty` は変換不要だが IsEmpty ではある
      case _ @Term.Select(x1: Term, _ @Term.Name("isEmpty")) => Some(x1, false)
      // `seq.size == 0`
      case _ @Term.ApplyInfix(
            Term.Select(x1: Term, _ @(Term.Name("size") | Term.Name("length"))),
            Term.Name("=="),
            Nil,
            List(Lit.Int(0))
          ) => Some((x1, true))
      case _ @Term.ApplyUnary(Term.Name("!"), NonEmpty(x1, _)) => Some((x1, true))
      case _ @Term.ApplyUnary(Term.Name("!"), IsDefined(x1, _)) => Some((x1, true))
      // option == None
      case _ @Term.ApplyInfix(x1: Term, _ @Term.Name("=="), Nil, List(Term.Name("None")))
          if isType(x1, classOf[Option[Any]]) => Some((x1, true))
      // None == option
      case _ @Term.ApplyInfix(Term.Name("None"), _ @Term.Name("=="), Nil, List(x1: Term))
          if isType(x1, classOf[Option[Any]]) => Some((x1, true))
      case _ => None
    }
  }
}

private object NonEmpty {
  def unapply(tree: Tree)(implicit doc: SemanticDocument): Option[(Term, Boolean)] = tree match {
    // `seq.nonEmpty` は変換不要だが NonEmpty ではある
    case _ @Term.Select(x1: Term, _ @Term.Name("nonEmpty")) => Some(x1, false)
    // `seq.size != 0` or `seq.size > 0`
    case _ @Term.ApplyInfix(
          Term.Select(x1: Term, _ @(Term.Name("size") | Term.Name("length"))),
          _ @(Term.Name("!=") | Term.Name(">")),
          Nil,
          List(Lit.Int(0))
        ) => Some((x1, true))
    // `seq.size > 1`
    case _ @Term.ApplyInfix(
          Term.Select(x1: Term, _ @(Term.Name("size") | Term.Name("length"))),
          _ @Term.Name(">="),
          Nil,
          List(Lit.Int(1))
        ) => Some((x1, true))
    // `seq.exists(_ => true)`
    case _ @Term.Apply(
          Term.Select(x1, _ @Term.Name("exists")),
          List(Term.Function((_, Lit.Boolean(true))))
        ) => Some((x1, true))
    // `seq.exists(Function.const(true))`
    case _ @Term.Apply(
          Term.Select(x1, _ @Term.Name("exists")),
          List(Term.Apply(Term.Select(Term.Name("Function"), Term.Name("const")), List(Lit.Boolean(true))))
        ) => Some((x1, true))
    case _ @Term.ApplyUnary(Term.Name("!"), IsEmpty(x1, _)) if !isType(x1, classOf[Option[Any]]) =>
      Some((x1, true))
    case _ => None
  }
}

private object IsDefined {
  def unapply(tree: Tree)(implicit doc: SemanticDocument, alignIsDefined: Boolean): Option[(Term, Boolean)] =
    tree match {
      // `option.isDefined` は変換不要だが IsDefined ではある
      case _ @Term.Select(x1: Term, _ @Term.Name("isDefined")) => Some(x1, false)
      // option != None
      case _ @Term.ApplyInfix(x1: Term, _ @Term.Name("!="), Nil, List(Term.Name("None"))) => Some((x1, true))
      // None != option
      case _ @Term.ApplyInfix(Term.Name("None"), _ @Term.Name("!="), Nil, List(x1: Term)) => Some((x1, true))
      case _ @NonEmpty(Term.Placeholder(), _) => None
      case _ @Term.ApplyUnary(Term.Name("!"), IsEmpty(x1, _)) if isType(x1, classOf[Option[Any]]) =>
        Some((x1, true))
      // option.nonEmpty => option.isDefined
      case _ @NonEmpty(x1, _) if isType(x1, classOf[Option[Any]]) => Some((x1, alignIsDefined))
      case _ => None
    }
}
