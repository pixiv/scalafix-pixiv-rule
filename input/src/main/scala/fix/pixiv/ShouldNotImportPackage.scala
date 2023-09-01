/*
rule = ShouldNotImportPackage
ShouldNotImportPackage.blackList = [
  { target = "^scala\\.collection\\.mutable.*$", importer = "^.+\\.pixiv$" }
]
 */
package fix.pixiv

import scala.collection.mutable/* assert: ShouldNotImportPackage
       ^^^^^^^^^^^^^^^^^^^^^^^^
scala.collection.mutable は fix.pixiv から呼び出されるべきではありません
*/

class ShouldNotImportPackage
 extends AnyRef {
  val map: mutable.Map[String, String] = mutable.Map("key" -> "value")
}
