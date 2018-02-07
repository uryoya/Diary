package com.uryoya.diary.repository.mysql

import doobie._
import cats.effect.IO
import com.uryoya.diary.Config.MysqlConfig
import com.uryoya.diary.config.mysql

object MysqlTransactors {
  lazy val master = createTransactor(mysql)

  private def createTransactor(mc: MysqlConfig) = {
    Transactor.fromDriverManager[IO](
      "com.mysql.jdbc.Driver",
      s"jdbc:mysql://${mc.host}:${mc.port}/${mc.db}?useSSL=false&requireSSL=false",
      mc.user,
      mc.password,
    )
  }

}
