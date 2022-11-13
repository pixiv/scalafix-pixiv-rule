package fix.pixiv

import org.mockito.Mockito.when
import org.mockito.{ArgumentMatchers, Mockito}

class MockitoThenToDo {
  class A {
    def hoge(): String = "hoge"
    def fuga: String = "fuga"
    def piyo(s: String): String = s"piyo: $s"
    def foo: String = "foo"
  }
  private val a = Mockito.mock(classOf[A])
  Mockito.doReturn("mock1").doReturn("mock2").when(a).hoge()
  Mockito.doReturn("mock").when(a).fuga
  Mockito.doAnswer { invocation => 
  val s = invocation.getArgument[String](0)
  s"mock: $s"
}.when(a).piyo(ArgumentMatchers.any())
  Mockito.doThrow(new RuntimeException("mock")).when(a).foo
  println(a.hoge())
  println(a.fuga)
  println(a.piyo(""))
  println(a.foo)
}
