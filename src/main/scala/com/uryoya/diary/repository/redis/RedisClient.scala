package com.uryoya.diary.repository.redis

import com.twitter.finagle.redis
import com.twitter.finagle.redis.util.{BufToString, StringToBuf}
import com.twitter.io.Buf
import com.twitter.util.Future

class RedisClient(underlying: redis.Client) {
  private implicit def anyToBuf: Any => Buf = a => StringToBuf(a.toString)

  def get(key: String): Future[Option[String]] = {
    underlying.get(StringToBuf(key)).map{ r =>
      r.map(buf => BufToString(buf))
    }
  }

  def hGetAll(key: String): Future[Map[String, String]] = {
    underlying.hGetAll(StringToBuf(key)).map { r =>
      r.map{
        case(k, v) => (BufToString(k), BufToString(v))
      }.toMap
    }
  }

  def hSet(key: String, field: String, value: String): Future[java.lang.Long] = {
    underlying.hSet(StringToBuf(key), StringToBuf(field), StringToBuf(value))
  }

  def del(key: String): Future[java.lang.Long] = {
    underlying.dels(Seq(StringToBuf(key)))
  }

  def expire(key: String, ttl: Long): Future[Boolean] = {
    underlying.expire(key, ttl).map(_.booleanValue)
  }
}
