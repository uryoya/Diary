package com.uryoya.diary

import com.twitter.app.App
import com.twitter.finagle.Http
import com.twitter.util.Await

object Server extends App {
  def main(): Unit = {

    val addr   = ":8080"
    val server = Http.server.serve(addr, new Api().service)

    onExit { Await.result(server.close) }

    Await.ready(server)
  }
}
