/*
rule = MockitoThenToDo
 */
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
  Mockito.when(a.hoge()).thenReturn("mock1").thenReturn("mock2")
  when(a.fuga).thenReturn("mock")
  Mockito.when(a.piyo(ArgumentMatchers.any())).thenAnswer { invocation =>
    val s = invocation.getArgument[String](0)
    s"mock: $s"
  }
  Mockito.when(a.foo).thenThrow(new RuntimeException("mock"))
  println(a.hoge())
  println(a.fuga)
  println(a.piyo(""))
  println(a.foo)
}
