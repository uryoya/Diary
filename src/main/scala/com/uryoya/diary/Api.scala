package com.uryoya.diary

import java.security.AccessControlException
import java.util.concurrent.TimeUnit

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Cookie, Request, Response}
import com.twitter.util.Duration
import com.uryoya.diary.controller.{AuthenticationController, UserController}
import com.uryoya.diary.request.{CreateUserRequest, SigninRequest, UserRequest}
import com.uryoya.diary.response.{MessageResponse, UserResponse}
import com.uryoya.diary.service.SessionService
import io.circe.generic.auto._
import io.finch.Endpoint
import io.finch._
import io.finch.circe._

class Api {
  type EitherSession = Either[AccessControlException, SessionService]
  val service: Service[Request, Response] = {
    val sessionKey = config.session.cookieName
    val requiredSession: Endpoint[EitherSession] = root.mapAsync { req =>
      SessionService.getFromCookie(req.cookies.get(sessionKey).getOrElse(new Cookie(sessionKey, ""))) map {
        case Some(session) => Right(session)
        case None => Left(new AccessControlException(""))
      }
    }

    // Authentication
    val signin: Endpoint[MessageResponse] =
      post("api" :: "signin" :: jsonBody[SigninRequest]) { req: SigninRequest =>
        AuthenticationController.signin(req) match {
          case Right((res, session)) => {
            val cookie = new Cookie(sessionKey, session.id)
            cookie.maxAge = Duration(config.session.maxAge, TimeUnit.SECONDS)
            Ok(res).withCookie(cookie)
          }
          case Left(e) => BadRequest(new IllegalArgumentException(e.message))
        }
      }

    val signout: Endpoint[MessageResponse] =
      post("api" :: "signout" :: requiredSession) { rs: EitherSession =>
        rs match {
          case Right(session) => Ok(AuthenticationController.signout(session))
            .withCookie(new Cookie(sessionKey, ""))
          case Left(e) => Unauthorized(e)
        }
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
      get("api" :: "users" :: requiredSession) { rs: EitherSession =>
        rs match {
          case Right(_) => Ok(UserController.users)
          case Left(e) => Unauthorized(e)
        }
      }

    val user: Endpoint[UserResponse] =
      get("api" :: "users" :: path[String] :: requiredSession) {
        (loginId: String, rs: EitherSession) =>
        rs match {
          case Right(_) => UserController.user(loginId) match {
            case Right(resp) => Ok(resp)
            case Left(e) => NotFound(new IllegalArgumentException(e.message))
          }
          case Left(e) => Unauthorized(e)
        }
      }

    val updateUser: Endpoint[UserResponse] =
      put("api" :: "users" :: path[String] :: requiredSession :: jsonBody[UserRequest]) {
        (loginId: String, rs: EitherSession, user: UserRequest) =>
          rs match {
            case Right(session) => UserController.updateUser(loginId, user, session) match {
              case Right(resp) => Ok(resp)
              case Left(e) => Unauthorized(new AccessControlException(e.message))
            }
            case Left(e) => Unauthorized(e)
          }
      }

    (
      signin
        :+: signout
        :+: createUser
        :+: users
        :+: user
        :+: updateUser
    ).toServiceAs[Application.Json]
  }
}
