package com.uryoya.diary

import java.security.AccessControlException

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Cookie, Request, Response}
import com.uryoya.diary.controller.AuthenticationController
import com.uryoya.diary.request.SigninRequest
import com.uryoya.diary.response.MessageResponse
import com.uryoya.diary.service.SessionService
import io.circe.generic.auto._
import io.finch.Endpoint
import io.finch._
import io.finch.circe._

class Api {
  val service: Service[Request, Response] = {
    val sessionKey = config.session.cookieName
    val requiredSession: Endpoint[Either[AccessControlException, SessionService]] = root.map { req =>
      req.cookies.get(sessionKey) match {
        case Some(cookie) => SessionService.getFromCookie(cookie) match {
          case Some(session) => Right(session)
          case None => Left(new AccessControlException(""))
        }
        case None => Left(new AccessControlException(""))
      }
    }

    val signin: Endpoint[MessageResponse] =
      post("api" :: "signin" :: jsonBody[SigninRequest]) { req: SigninRequest =>
        AuthenticationController.signin(req) match {
          case Right((res, session)) => Ok(res).withCookie(new Cookie(sessionKey, session.id))
          case Left(e) => BadRequest(new IllegalArgumentException(e.message))
        }
      }

    val signout: Endpoint[MessageResponse] =
      post("api" :: "signout" :: requiredSession) { rs: Either[AccessControlException, SessionService] =>
        rs match {
          case Right(session) => Ok(AuthenticationController.signout(session))
            .withCookie(new Cookie(sessionKey, ""))
          case Left(e) => Unauthorized(e)
        }
      }

    (signin :+: signout).toServiceAs[Application.Json]
  }
}
