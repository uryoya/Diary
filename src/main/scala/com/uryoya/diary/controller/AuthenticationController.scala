package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.request._
import com.uryoya.diary.response._
import com.uryoya.diary.repository.mysql._
import com.uryoya.diary.service.AuthenticationService

object AuthenticationController {
  def Signin(req: SigninRequest): Either[InvalidRequest, MessageResponse] = {
    UserRepository.getUser(req.login) match {
      case Some(user) => if (AuthenticationService.passwordVerify(req.password, user.passwordHash)) {
        Right(MessageResponse("Success."))
      } else {
        Left(InvalidRequest("Password can not verified."))
      }
      case None => Left(InvalidRequest("User not found."))
    }
  }

//  def Signout(sessionId: SessionId): Either[InvalidRequest, MessageResponse] = {
//    // pass
//  }
}
