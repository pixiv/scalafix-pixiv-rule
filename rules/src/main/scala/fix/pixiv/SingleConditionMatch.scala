package fix.pixiv

import scala.meta.Term.Match
import scala.meta.{Case, Defn, Pat, Term, Tree}

import scalafix.Patch
import scalafix.v1.{SemanticDocument, SemanticRule}

class SingleConditionMatch extends SemanticRule("SingleConditionMatch") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      // case a => a
      case t @ Match(_, List(Case(Pat.Var(pName), _, b: Term.Name))) if b.value == pName.value =>
        Patch.replaceTree(t, t.expr.toString())
      // case a => {f(a); g(a)}
      case t @ Match(expr, List(Case(Pat.Var(pName), _, b: Term.Block))) =>
        toBlockPatch(t, pName.value, expr, b)
      // case a => f(a)
      case t @ Match(expr, List(Case(Pat.Var(pName), _, b: Term))) =>
        toSingleReplaceIfOnceUseVal(t, pName.value, expr, b)
      // case Some(a) => f(a)
      case t @ Match(expr, List(Case(Pat.Extract(fun: Term.Name, List(Pat.Var(pName))), _, b: Term)))
          if fun.value == "Some" =>
        Patch.replaceTree(
          t,
          Term.Apply(
            Term.Select(expr, Term.Name("foreach")),
            List(Term.Block(List(Term.Function(List(Term.Param(Nil, pName, None, None)), b))))
          ).toString()
        )
      case _ => Patch.empty
    }
  }.reverse.asPatch

  // TODO: (a \ "test") の \ が消える問題に対応する
  private def toSingleReplaceIfOnceUseVal(from: Tree, valName: String, rhs: Term, body: Term): Patch = {
    body.collect {
      case v: Term.Name if v.value == valName => v
    } match {
      case List(name) =>
        // TODO: 同じブロック内に変数名と同一の文字列リテラルがあったら死ぬので直す
        Patch.replaceTree(from, body.toString().replaceFirst(name.value, rhs.toString()))
      case _ => toBlockPatch(from, valName, rhs, body)
    }
  }

  private def toBlockPatch(from: Tree, defVal: Defn.Val, body: Term): Patch = {
    body match {
      case Term.Block(list) => Patch.replaceTree(from, Term.Block(List(defVal).appendedAll(list)).toString())
      case body => Patch.replaceTree(from, Term.Block(List(defVal, body)).toString())
    }
  }

  private def toBlockPatch(from: Tree, valName: String, rhs: Term, body: Term): Patch = {
    toBlockPatch(from, Defn.Val(Nil, List(Pat.Var(Term.Name(valName))), None, rhs), body)
  }
}
