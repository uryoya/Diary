package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.request.CreateUserRequest
import com.uryoya.diary.response.UserResponse
import com.uryoya.diary.service.{AuthenticationService, SessionService}

object UserController {
  def createUser(newUser: CreateUserRequest): Either[InvalidRequest, UserResponse] = {
    if (!validateUserInfo(newUser) || UserRepository.exists(newUser.login))
      return Left(InvalidRequest("Can not create user."))
    val passwordHash = AuthenticationService.passwordHash(newUser.password)
    UserRepository.addUser(newUser.login, passwordHash, newUser.name, "", newUser.accessToken, false)
    UserRepository.getUser(newUser.login) match {
      case Some(user) => Right(UserResponse(user.id, user.login, user.name, user.admin))
      case None => Left(InvalidRequest("Can not create user."))
    }
  }

  def users: List[UserResponse] = {
    UserRepository.getAllUser.map { user =>
      UserResponse(user.id, user.login, user.name, user.admin)
    }
  }

  def user(loginId: String): Either[InvalidRequest, UserResponse] = {
    UserRepository.getUser(loginId) match {
      case Some(user) => Right(UserResponse(user.id, user.login, user.name, user.admin))
      case None => Left(InvalidRequest("User Not Found."))
    }
  }

  def validateUserInfo(user: CreateUserRequest): Boolean =
    user.login != "" && user.password != "" && user.name != ""
}
