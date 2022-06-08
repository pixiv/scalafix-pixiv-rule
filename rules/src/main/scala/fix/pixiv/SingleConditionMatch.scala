package fix.pixiv

import scala.meta.Term.Match
import scala.meta.{Case, Defn, Pat, Term, Tree}

import scalafix.Patch
import scalafix.v1.{SemanticDocument, SemanticRule}

class SingleConditionMatch extends SemanticRule("SingleConditionMatch") {

  override def fix(implicit doc: SemanticDocument): Patch = doc.tree.collect {
    // case a => a
    // SingleReturn の場合は Match が含まれることはない
    case SimpleReturn(t) => Patch.replaceTree(t, t.expr.toString)
    // case a => {f(a); g(a)}
    case HasBlock(t, pName, expr, b) if !hasMatch(b) =>
      toBlockPatch(t, pName.value, expr, b)
    // case a => f(a)
    case HasSingleState(t, pName, expr, b) if !hasMatch(b) =>
      toSingleReplaceIfOnceUseVal(t, pName.value, expr, b)
    // case Some(a) => f(a)
    case UnapplySome(t, pName, expr, b) if !hasMatch(b) =>
      Patch.replaceTree(
        t,
        Term.Apply(
          Term.Select(expr, Term.Name("foreach")),
          List(Term.Block(List(Term.Function(List(Term.Param(Nil, pName, None, None)), b))))
        ).toString
      )
    case _ =>
      Patch.empty
  }.asPatch

  /**
    * 入れ子構造になった簡略化可能な Match 式を変換すると構文木が破壊されるので、同様のパターンを内部に含まないことを確認する
    */
  private def hasMatch(tree: Tree): Boolean = {
    // 一つでも見つけたらその後の探索を行う必要はない
    var has = false
    tree.collect {
      case _ if has => // 何もしない
      case SimpleReturn(_) => has = true
      case HasBlock(_, _, _, _) => has = true
      case HasSingleState(_, _, _, _) => has = true
      case UnapplySome(_, _, _, _) => has = true
    }
    has
  }

  private def toSingleReplaceIfOnceUseVal(from: Tree, valName: String, rhs: Term, body: Term): Patch = {
    body.collect {
      case v: Term.Name if v.value == valName => v
    } match {
      case List(name) =>
        // String#replace 系のメソッドでは、 "\" が消滅するので "\\" に置換してあげる
        Patch.replaceTree(from, body.toString.replaceFirst(name.value, rhs.toString.replaceAll("\\\\", "\\\\\\\\")))
      case _ => toBlockPatch(from, valName, rhs, body)
    }
  }

  private def toBlockPatch(from: Tree, valName: String, rhs: Term, body: Term): Patch = {
    toBlockPatch(from, Defn.Val(Nil, List(Pat.Var(Term.Name(valName))), None, rhs), body)
  }

  private def toBlockPatch(from: Tree, defVal: Defn.Val, body: Term): Patch = {
    // Block に変換する際、改行を入れないと前の処理の引数として渡されてしまうことがある
    body match {
      case Term.Block(list) => Patch.replaceTree(from, "\n" + Term.Block(List(defVal).appendedAll(list)).toString)
      case body => Patch.replaceTree(from, "\n" + Term.Block(List(defVal, body)).toString)
    }
  }
}

private object SimpleReturn {
  def unapply(tree: Tree): Option[Term.Match] = tree match {
    case t @ Match(_, List(Case(Pat.Var(pName), _, b: Term.Name))) if b.value == pName.value => Some(t)
    case _ => None
  }
}

private object HasBlock {
  def unapply(tree: Tree): Option[(Match, Term.Name, Term, Term.Block)] = tree match {
    case t @ Match(expr, List(Case(Pat.Var(pName), _, b: Term.Block))) => Some(t, pName, expr, b)
    case _ => None
  }
}

private object HasSingleState {
  def unapply(tree: Tree): Option[(Match, Term.Name, Term, Term)] = tree match {
    case t @ Match(expr, List(Case(Pat.Var(pName), _, b: Term))) => Some(t, pName, expr, b)
    case _ => None
  }
}

private object UnapplySome {
  def unapply(tree: Tree): Option[(Match, Term.Name, Term, Term)] = tree match {
    case t @ Match(expr, List(Case(Pat.Extract(fun: Term.Name, List(Pat.Var(pName))), _, b: Term)))
        if fun.value == "Some" => Some(t, pName, expr, b)
    case _ => None
  }
}
