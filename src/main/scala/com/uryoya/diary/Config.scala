package com.uryoya.diary

import com.uryoya.diary.Config._
import pureconfig._

final case class Config(
  mysql: MysqlConfig,
  redis: RedisConfig,
  github: GitHubAppKeys,
  session: SessionConfig,
)

object Config {
  def load: Config = loadConfigOrThrow[Config]

  final case class SessionConfig(cookieName: String = "SID", maxAge: Long)
  final case class MysqlConfig(host: String, port: Int, user: String, password: String, db: String)
  final case class RedisConfig(host: String, port: Int)
  final case class GitHubAppKeys(clientId: String, clientSecret: String)
}

