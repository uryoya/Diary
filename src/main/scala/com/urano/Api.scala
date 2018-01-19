package com.urano

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.urano.entity.{HelloWorldRequest, HelloWorldResponse}
import io.circe.generic.auto._
import io.finch.Endpoint
import io.finch._
import io.finch.circe._

class Api {
  val service: Service[Request, Response] = {
    // $ curl localhost:8080/helloworld
    // {"hello":"urano"}
    val helloWorld1: Endpoint[HelloWorldResponse] =
      get("helloworld") {
        Ok(HelloWorldResponse(hello = "world1"))
      }

    // $ curl -d '{"hello":"urano"}' localhost:8080/hello/world
    // {"hello":"urano"}
    val helloWorld2: Endpoint[HelloWorldResponse] =
      post("hello" :: "world" :: jsonBody[HelloWorldRequest]) { req: HelloWorldRequest =>
        Ok(HelloWorldResponse(hello = req.hello))
      }

    (helloWorld1 :+: helloWorld2).toServiceAs[Application.Json]
  }
}
