package com.uryoya.diary.service

import java.security.AccessControlException

import com.twitter.finagle.http.Cookie
import com.twitter.util.{Await, Future}
import com.uryoya.diary.config
import com.uryoya.diary.entity.SessionId
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.redis.{Redis, RedisClient}

case class SessionService(id: SessionId, data: Map[String, String]) {
  // please see Play: play.api.mvc.Http
  def get(key: String): Option[String] = data.get(key)
  def isEmpty: Boolean = data.isEmpty
  // def +(kv: (String, String))
  def -(key: String): SessionService = copy(data = data - key)
  def apply(key: String) = data(key)
  def remove: Unit = SessionService.removeSession(id)
}

object SessionService {
  private lazy val redis: RedisClient = Redis.newClient(config.redis)
  def COOKIE_NAME: SessionId = config.session.cookieName

  def newSession(key: String, value: String, user: User): SessionService = {
    val newId = generateSessionId(user)
    Await.result(redis.hSet(newId, key, value))
    Await.result(redis.expire(newId, config.session.maxAge))
    SessionService(newId, Map(key -> value))
  }

  def removeSession(id: SessionId): Unit = {
    Await.ready(redis.del(id))
  }

  /**
    * generateSessionIdで生成される文字列と同じ形式になっているか？
    *
    * ハッシュ化しているので、ハッシュ化された文字列の条件でバリデーションする。
    * （バイト列をHexで文字列化。長さはsha256を使っているので64文字で固定。）
    */
  def isTrueSession(id: SessionId): Boolean = id.matches("""^[0-9|a-f]{64}$""")

  /**
    * クッキーからセッションIDを取り出し、Redisと照合してセッション変数を取り出す。
    */
  def getFromCookie(cookie: Cookie): Future[Option[SessionService]] = {
    if (!isTrueSession(cookie.value)) return Future.None
    redis.hGetAll(cookie.value).map { value =>
      if (value.isEmpty) None
      else Some(SessionService(cookie.value, value))
    }
  }

  def generateSessionId(user: User): SessionId = {
    val sha256 = java.security.MessageDigest.getInstance("SHA-256")
    val payload: String =
      user.login +
      java.time.OffsetDateTime.now.toString +
      java.security.SecureRandom.getSeed(8).toString
    sha256.digest(payload.getBytes).map("%02x".format(_)).mkString
  }
}
