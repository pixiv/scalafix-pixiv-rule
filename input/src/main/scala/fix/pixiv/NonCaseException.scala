/*
rule = NonCaseException
 */
package fix.pixiv

case class NonCaseException(msg: String) extends RuntimeException(msg) /* assert: NonCaseException
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
case class として Exception を継承することは推奨されません: NonCaseException
     */
