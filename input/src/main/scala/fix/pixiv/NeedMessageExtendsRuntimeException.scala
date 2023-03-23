/*
rule = NeedMessageExtendsRuntimeException
 */
package fix.pixiv

class GreenException extends RuntimeException("message") {}

class RedException extends RuntimeException {} /* assert: NeedMessageExtendsRuntimeException
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
RuntimeException を継承したクラスを作る際にはメッセージを付与してください: RedException
 */
