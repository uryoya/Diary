package com.uryoya.diary.controller

import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.request._
import com.uryoya.diary.response._
import com.uryoya.diary.repository.mysql._
import com.uryoya.diary.service.{AuthenticationService, SessionService}

object AuthenticationController {
  def signin(req: SigninRequest): Either[InvalidRequest, (MessageResponse, User)] = {
    UserRepository.getUser(req.login) match {
      case Some(user) =>
       if (AuthenticationService.passwordVerify(req.password, user.passwordHash)) {
        Right(MessageResponse("Success."), user)
      } else {
        Left(InvalidRequest("Password can not verified."))
      }
      case None => Left(InvalidRequest("User not found."))
    }
  }

  def signout(session: SessionService): MessageResponse = {
    session.remove
    MessageResponse("Success.")
  }
}
