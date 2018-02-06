package com.uryoya.diary.service

import java.security.AccessControlException

import com.twitter.finagle.http.Cookie
import com.twitter.util.Await
import com.uryoya.diary.config
import com.uryoya.diary.entity.SessionId
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

  def newSession(key: String, value: String): SessionService = {
    val newId = generateSessionId
    Await.result(redis.hSet(newId, key, value))
    Await.result(redis.expire(newId, config.session.maxAge))
    SessionService(newId, Map(key -> value))
  }

  def removeSession(id: SessionId): Unit = {
    Await.ready(redis.del(id))
  }

  /**
    * クッキーからセッションIDを取り出し、Redisと照合してセッション変数を取り出す。
    */
  def getFromCookie(cookie: Cookie): Option[SessionService] = {
    val value = Await.result(redis.hGetAll(cookie.value))
    if (value.isEmpty) None
    else Some(SessionService(cookie.value, value))
  }

  private def generateSessionId: SessionId = {
    "random session id"
  }
}
