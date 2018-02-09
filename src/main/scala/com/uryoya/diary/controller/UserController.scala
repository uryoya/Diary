package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.response.UserResponse
import com.uryoya.diary.service.SessionService

object UserController {
  def users: List[UserResponse] = {
    UserRepository.getAllUser.map { user =>
      UserResponse(user.id, user.login, user.name, user.admin)
    }
  }

  def detail(session: SessionService): Either[InvalidRequest, UserResponse] = {
    UserRepository.getUser(session.get("login").getOrElse("")) match {
      case Some(user) => Right(UserResponse(user.id, user.login, user.name, user.admin))
      case None => Left(InvalidRequest("User Not Found."))
    }
  }
}
