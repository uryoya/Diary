package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.request.CreateUserRequest
import com.uryoya.diary.response.UserResponse
import com.uryoya.diary.service.{AuthenticationService, SessionService}

object UserController {
  def createUser(newUser: CreateUserRequest): Either[InvalidRequest, UserResponse] = {
    val passwordHash = AuthenticationService.passwordHash(newUser.password)
    val id = UserRepository.addUser(newUser.login, passwordHash, newUser.name, "", newUser.accessToken, false)
    UserRepository.getUser(id) match {
      case Some(user) => Right(UserResponse(user.id, user.login, user.name, user.admin))
      case None => Left(InvalidRequest("Can not create user."))
    }
  }

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
