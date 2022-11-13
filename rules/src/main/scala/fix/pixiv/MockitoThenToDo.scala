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

  def apply(target: Term.Name, func: Term.Name, args: Option[List[Term]], thenTerm: List[(String, Term)]): Term = {
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
      case Some(args) =>
        Term.Apply(
          Term.Select(
            Term.Apply(
              Term.Select(
                doTherm,
                Term.Name("when")
              ),
              List(func)
            ),
            target
          ),
          args
        )
      case None =>
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
    }
  }
}

private object MockitoThen {
  val methodNames: List[String] =
    List("thenReturn", "thenAnswer", "thenThrow")
  def unapply(tree: Tree): Option[(Term.Name, Term.Name, Option[List[Term]], List[(String, Term)])] = tree match {
    case Term.Apply(
          Term.Select(
            MockitoThenInternal(target, func, args, thenTerm: List[(String, Term)]),
            Term.Name(method)
          ),
          List(result)
        ) if methodNames.contains(method) => Some(target, func, args, thenTerm :+ (method, result))
    case _ => None
  }

  private object MockitoThenInternal {
    def unapply(tree: Tree): Option[(Term.Name, Term.Name, Option[List[Term]], List[(String, Term)])] = tree match {
      case Term.Apply(
            Term.Select(
              MockitoThenInternal(target, func, args, thenTerm: List[(String, Term)]),
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
            List(Term.Apply(Term.Select(func: Term.Name, target), args))
          ) => Some(target, func, Some(args), Nil)
      case Term.Apply(
            (Term.Select(
              Term.Name("Mockito"),
              Term.Name("when")
            ) | Term.Name("when")),
            List(Term.Select(func: Term.Name, target))
          ) => Some(target, func, None, Nil)
      case _ => None
    }
  }

}
