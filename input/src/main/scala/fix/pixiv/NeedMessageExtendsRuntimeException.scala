/*
rule = NeedMessageExtendsRuntimeException
 */
package fix.pixiv

class GreenException extends RuntimeException("message") {}

class RedException extends RuntimeException {}/* assert: NeedMessageExtendsRuntimeException
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
RuntimeException を継承したクラスを作る際にはメッセージを付与してください: RedException
 */

// RuntimeExceptionを継承したクラスの継承は無視しています。
// ref: https://github.com/pixiv/scalafix-pixiv-rule/pull/64#discussion_r1145841349
class TestClass extends RedException {}
