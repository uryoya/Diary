package com.uryoya.diary.repository.mysql

import doobie._
import cats.effect.IO

object MysqlTransactors {
  lazy val master = createTransactor

  private def createTransactor = {
    val mc = getMysqlConfig
    Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      s"jdbc:mysql://${mc.host}:${mc.port}/${mc.db}",
      mc.user,
      mc.password,
    )
  }

  /**
    * MySQLの設定を環境変数から取り込む。
    *
    * Scalaで設定ファイルを扱う方法は検討中なので、とりあえず環境変数から取り込む。
    * `sys.env()`が失敗した場合はそのまま例外を創出して終了させる。
    */
  private def getMysqlConfig: MysqlConfig = MysqlConfig(
    sys.env("MYSQL_HOST"),
    sys.env("MYSQL_PORT").toInt,
    sys.env("MYSQL_DB"),
    sys.env("MYSQL_USER"),
    sys.env("MYSQL_PASSWORD"),
  )
}

case class MysqlConfig(host: String, port: Int, db: String, user: String, password: String)
