package com.uryoya.diary.service

object AuthenticationService {
  /**
    * パスワードのチェックをする
    *
    * 評価時間が一定でないのでタイミング攻撃の脆弱性あり。
    */
  def passwordVerify(password: String, hash: String): Boolean = passwordHash(password) == hash

  /**
    * パスワードハッシュを生成する
    *
    * TODO: HMACやSaltを使っていないので後で対応。
    */
  def passwordHash(password: String): String = {
    val sha256 = java.security.MessageDigest.getInstance("SHA-256")
    sha256.digest(password.getBytes).map("%02x".format(_)).mkString
  }
}
