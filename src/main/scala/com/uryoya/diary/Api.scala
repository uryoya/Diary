package com.uryoya.diary

import java.security.AccessControlException
import java.util.concurrent.TimeUnit

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Cookie, Request, Response}
import com.twitter.util.Duration
import com.uryoya.diary.controller.{AuthenticationController, UserController}
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.request.{CreateUserRequest, SigninRequest, UserRequest}
import com.uryoya.diary.response.{MessageResponse, UserResponse}
import com.uryoya.diary.service.SessionService
import io.circe.generic.auto._
import io.finch.Endpoint
import io.finch._
import io.finch.circe._
import shapeless.HNil

class Api {
  val service: Service[Request, Response] = {
    val sessionKey = config.session.cookieName
    val cookieMaxAge = Some(Duration(config.session.maxAge, TimeUnit.SECONDS))
    val sessionAsCookie = (session: SessionService) =>
      new Cookie(sessionKey, session.id, maxAge = cookieMaxAge)
    val sessionRevokeAsCookie = // Cookieは Max-Age を`0`に設定すると削除される
      new Cookie(sessionKey, "", maxAge = Some(Duration(0, TimeUnit.SECONDS)))

    val auth: Endpoint[HNil] =
      cookie(sessionKey).mapOutputAsync( _cookie =>
        SessionService.getFromCookie(_cookie)
          .map(_.fold(Unauthorized(new Exception("Unknown user.")): Output[HNil])(_ => Ok(HNil)))
      ).handle {
        case e: Error.NotPresent => Unauthorized(e)
      }

    val authWithUser: Endpoint[User] =
      cookie(sessionKey).mapOutputAsync( _cookie =>
        SessionService.getFromCookie(_cookie) map { maybeSessionService =>
          val maybeOutput =
            for {
              session <- maybeSessionService
              loginId <- session.get("login")
              signinUser <- UserRepository.getUser(loginId)
            } yield Ok(signinUser)
          maybeOutput.getOrElse(Unauthorized(new Exception("Unknown user.")))
        }
      ).handle {
        case e: Error.NotPresent => Unauthorized(e)
      }

    val authWithSession: Endpoint[SessionService] =
      cookie(sessionKey).mapOutputAsync( _cookie =>
        SessionService.getFromCookie(_cookie)
          .map(_.fold(Unauthorized(new Exception("Unknown user.")): Output[SessionService])(Ok))
      ).handle {
        case e: Error.NotPresent => Unauthorized(e)
      }

    // Authentication
    val signin: Endpoint[MessageResponse] =
      post("api" :: "signin" :: jsonBody[SigninRequest]) { req: SigninRequest =>
        AuthenticationController.signin(req) match {
          case Right((resp, user)) =>
            val session = SessionService.newSession("login", user.login, user)
            Ok(resp)
              .withCookie(sessionAsCookie(session)) case Left(e) => BadRequest(new IllegalArgumentException(e.message))
        }
      }

    val signout: Endpoint[MessageResponse] =
      post("api" :: "signout" :: authWithSession) { session: SessionService =>
        Ok(AuthenticationController.signout(session))
          .withCookie(sessionRevokeAsCookie)
      }

    // User
    val createUser: Endpoint[UserResponse] =
      post("api" :: "users" :: jsonBody[CreateUserRequest]) { req: CreateUserRequest =>
        UserController.createUser(req) match {
          case Right(res) => Ok(res)
          case Left(e) => BadRequest(new IllegalArgumentException(e.message))
        }
      }

    val users: Endpoint[List[UserResponse]] =
      get("api" :: "users" :: auth) {
        Ok(UserController.users)
      }

    val user: Endpoint[UserResponse] =
      get("api" :: "users" :: path[String] :: auth) { loginId: String =>
          UserController.user(loginId) match {
            case Right(resp) => Ok(resp)
            case Left(e) => NotFound(new IllegalArgumentException(e.message))
          }
      }

    val updateUser: Endpoint[UserResponse] =
      put("api" :: "users" :: path[String] :: jsonBody[UserRequest] :: authWithUser) {
        (loginId: String, userReq: UserRequest, signinUser: User) =>
          UserController.updateUser(loginId, userReq, signinUser) match {
            case Right(resp) => Ok(resp)
            case Left(e) => Forbidden(new AccessControlException(e.message))
          }
      }

    val updateUserAvatar: Endpoint[UserResponse] =
      put("api" :: "users" :: path[String] :: "avatar" :: binaryBody :: authWithUser) {
        (loginId: String, img: Array[Byte], signinUser: User) =>
          UserController.updateUserAvatar(loginId, img, signinUser) match {
            case Right(resp) => Ok(resp)
            case Left(e) => Forbidden(new AccessControlException(e.message))
          }
      }

    val deleteUser: Endpoint[MessageResponse] =
      delete("api" :: "users" :: path[String] :: authWithUser) {
        (loginId: String, signinUser: User) =>
          UserController.deleteUser(loginId, signinUser) match {
            case Right(resp) => Ok(resp)
            case Left(e) => Forbidden(new AccessControlException(e.message))
          }
      }

    (
      signin
        :+: signout
        :+: createUser
        :+: users
        :+: user
        :+: updateUser
        :+: updateUserAvatar
        :+: deleteUser
    ).toServiceAs[Application.Json]
  }
}
