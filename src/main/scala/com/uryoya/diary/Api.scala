package com.uryoya.diary

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Cookie, Request, Response}
import com.uryoya.diary.controller.AuthenticationController
import com.uryoya.diary.entity.{HelloWorldRequest, HelloWorldResponse}
import com.uryoya.diary.request.SigninRequest
import com.uryoya.diary.response.MessageResponse
import io.circe.generic.auto._
import io.finch.Endpoint
import io.finch._
import io.finch.circe._

class Api {
  val service: Service[Request, Response] = {
    val session: Endpoint[String] = root.map { req =>
      req.cookies
        .get("SESSION")
        .headOption.map(_.value.toString)
        .getOrElse("")
    }

    val signin: Endpoint[MessageResponse] =
      post("api" :: "signin" :: jsonBody[SigninRequest]) { req: SigninRequest =>
        AuthenticationController.signin(req) match {
          case Right(res) => Ok(res).withCookie(new Cookie("SESSION", "SESSIONID"))
          case Left(e) => BadRequest(new IllegalArgumentException(e.message))
        }
      }
    // $ curl localhost:8080/helloworld
    // {"hello":"urano"}
    val helloWorld1: Endpoint[HelloWorldResponse] =
      get("helloworld") {
        Ok(HelloWorldResponse(hello = "world1"))
      }

    // $ curl -d '{"hello":"urano"}' localhost:8080/hello/world
    // {"hello":"urano"}
    val helloWorld2: Endpoint[HelloWorldResponse] =
      post("hello" :: "world" :: jsonBody[HelloWorldRequest] :: session) { (req: HelloWorldRequest, session: String) =>
        val cookie = new Cookie("kafu", "chino")
        println(session)
        Ok(HelloWorldResponse(hello = req.hello)).withCookie(cookie)
      }

    (signin :+: helloWorld1 :+: helloWorld2).toServiceAs[Application.Json]
  }
}
