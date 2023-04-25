/*
rule = NamingConventionPackage
NamingConventionPackage.convention = [
  ["^.+\\.pixiv$", "^.+Package$"]
]
 */
package fix.pixiv

class NamingConventionPackage /* assert: NamingConventionPackage
^
class NamingConventionPackage は fix.pixiv パッケージに実装すべきではありません
*/
 extends AnyRef {
  val value: String = "string"
}