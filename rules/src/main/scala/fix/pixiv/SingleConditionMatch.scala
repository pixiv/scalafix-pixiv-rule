package fix.pixiv

import scala.meta.Term.{Match, Name}
import scala.meta.{Defn, Pat, Term}

import scalafix.Patch
import scalafix.v1.{SemanticDocument, SemanticRule}

class SingleConditionMatch extends SemanticRule("SingleConditionMatch") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t: Match =>
        t.cases match {
          case List(state) =>
            state.pat match {
              case p: Pat.Var =>
                state.body match {
                  // case a => a
                  case b: Term.Name if b.value == p.name.value =>
                    Patch.replaceTree(t, t.expr.toString())
                  // TODO: 複数ブロックの場合に対応
                  case _: Term.Block => Patch.empty
                  // case a => f(a)
                  case b: Term =>
                    var term: Option[Term.Name] = None
                    var multiple = false
                    b.collect {
                      // 変数が使われた式だったら term にポジションを保存する
                      case v: Term.Name if term.isEmpty && !multiple && v.value == p.name.value =>
                        term = Some(v)
                      // 複数回変数が使われていたら書き換えを行わない
                      case v: Term.Name if !multiple && v.value == p.name.value =>
                        multiple = true
                    }
                    term match {
                      case Some(name) if !multiple =>
                        Patch.replaceTree(t, b.toString().replaceFirst(name.value, t.expr.toString()))
                      case _ =>
                        Patch.replaceTree(
                          t,
                          Term.Block(
                            List(
                              Defn.Val(Nil, List(Pat.Var(Name(p.toString()))), None, t.expr),
                              b
                            )
                          ).toString()
                        )
                    }
                }
              // TODO: 抽出子が利用されているパターンに対応
              case p => Patch.addAround(p, "[", s": ${p.getClass}]")
            }
          case _ => Patch.empty
        }
      // case t => Patch.addAround(t, "[", s": ${t.getClass}]")
    }
  }.asPatch
}
