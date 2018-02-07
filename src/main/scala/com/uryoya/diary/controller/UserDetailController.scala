package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.response.UserDetailResponse
import com.uryoya.diary.service.SessionService

object UserDetailController {
  def detail(session: SessionService): Either[InvalidRequest, UserDetailResponse] = {
    UserRepository.getUser(session.get("login").getOrElse("")) match {
      case Some(user) => Right(UserDetailResponse(user.id, user.login, user.name, user.admin))
      case None => Left(InvalidRequest("User Not Found."))
    }
  }
}
