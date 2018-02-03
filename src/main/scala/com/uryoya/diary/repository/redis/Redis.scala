package com.uryoya.diary.repository.redis

import com.twitter.finagle.{Redis => FinagleRedis}
import com.uryoya.diary.Config.RedisConfig

object Redis {
  def newClient(conf: RedisConfig): RedisClient =
    new RedisClient(FinagleRedis.newRichClient(conf.host + ":" + conf.port.toString))
}
