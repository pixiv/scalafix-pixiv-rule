package fix.pixiv

import scala.meta.{Term, Tree}

import scalafix.Patch
import scalafix.v1.{SemanticDocument, SemanticRule}
import util.TreeUtil.ShallowCollectTree

class MockitoThenToDo extends SemanticRule("MockitoThenToDo") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.shallowCollect {
      case t @ MockitoThen(target, func, args, thenTerm) =>
        Some(Patch.replaceTree(t, MockitoDo(target, func, args, thenTerm).syntax))
      case _ => None
    }.asPatch
  }
}

private object MockitoDo {
  private val mathodNameConverter = (s: String) =>
    s match {
      case "thenReturn" => "doReturn"
      case "thenAnswer" => "doAnswer"
      case "thenThrow" => "doThrow"
      case method => throw new RuntimeException(s"Mockito の then メソッドでない可能性があります: $method")
    }

  def apply(target: Term.Name, func: Term.Name, args: List[List[Term]], thenTerm: List[(String, Term)]): Term = {
    val doTherm = thenTerm.foldLeft[Term] {
      Term.Name("Mockito")
    } { case (term, (method, result)) =>
      Term.Apply(
        Term.Select(
          term,
          Term.Name(mathodNameConverter(method))
        ),
        List(result)
      )
    }
    args match {
      case Nil =>
        Term.Select(
          Term.Apply(
            Term.Select(
              doTherm,
              Term.Name("when")
            ),
            List(func)
          ),
          target
        )
      case list: List[List[Term]] =>
        list.foldLeft[Term](
          Term.Select(
            Term.Apply(
              Term.Select(
                doTherm,
                Term.Name("when")
              ),
              List(func)
            ),
            target
          )
        ) {
          case (select, arg) => Term.Apply(select, arg)
        }
    }
  }
}

private object MockitoThen {
  val methodNames: List[String] =
    List("thenReturn", "thenAnswer", "thenThrow")
  def unapply(tree: Tree): Option[(Term.Name, Term.Name, List[List[Term]], List[(String, Term)])] = tree match {
    case Term.Apply(
          Term.Select(
            MockitoThen(target, func, args, thenTerm: List[(String, Term)]),
            Term.Name(method)
          ),
          List(result)
        ) if methodNames.contains(method) =>
      Some(target, func, args, thenTerm :+ (method, result))
    case Term.Apply(
          (Term.Select(
            Term.Name("Mockito"),
            Term.Name("when")
          ) | Term.Name("when")),
          List(MockitoThenInternal(target, func, args))
        ) => Some(target, func, args, Nil)
    case _ => None
  }

  private object MockitoThenInternal {
    def unapply(tree: Tree): Option[(Term.Name, Term.Name, List[List[Term]])] = tree match {
      case Term.Apply(
            MockitoThenInternal(target, func, args),
            arg
          ) => Some(target, func, args :+ arg)
      case Term.Select(func: Term.Name, target) => Some(target, func, Nil)
    }
  }
}
