package com.uryoya.diary.controller

import com.uryoya.diary.entity._
import com.uryoya.diary.request._
import com.uryoya.diary.response._
import com.uryoya.diary.repository.mysql._
import com.uryoya.diary.service.{AuthenticationService, SessionService}

object AuthenticationController {
  def signin(req: SigninRequest): Either[InvalidRequest, (MessageResponse, SessionService)] = {
    UserRepository.getUser(req.login) match {
      case Some(user) => if (AuthenticationService.passwordVerify(req.password, user.passwordHash)) {
        val session = SessionService.newSession("login", user.login, user)
        Right(MessageResponse("Success."), session)
      } else {
        Left(InvalidRequest("Password can not verified."))
      }
      case None => Left(InvalidRequest("User not found."))
    }
  }

  def signout(session: SessionService): MessageResponse = {
    session.remove
    MessageResponse("Success.") // ここにサーバ側のセッションIDを消す処理を書く
  }
}
