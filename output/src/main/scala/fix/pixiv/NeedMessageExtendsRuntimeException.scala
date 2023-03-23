package fix.pixiv

class GreenException extends RuntimeException("message") {}

class RedException extends RuntimeException {}

// RuntimeExceptionを継承したクラスの継承は無視しています。
// ref: https://github.com/pixiv/scalafix-pixiv-rule/pull/64#discussion_r1145841349
class TestClass extends RedException {}
