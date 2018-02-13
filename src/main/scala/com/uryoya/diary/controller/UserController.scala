package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.request.{CreateUserRequest, UserRequest}
import com.uryoya.diary.response.UserResponse
import com.uryoya.diary.service.{AuthenticationService, AvatarService, SessionService}

object UserController {
  def createUser(newUser: CreateUserRequest): Either[InvalidRequest, UserResponse] = {
    if (!validateUserInfo(newUser) || UserRepository.exists(newUser.login))
      return Left(InvalidRequest("Can not create user."))
    val passwordHash = AuthenticationService.passwordHash(newUser.password)
    UserRepository.addUser(newUser.login, passwordHash, newUser.name, "", newUser.accessToken, false)
    UserRepository.getUser(newUser.login) match {
      case Some(user) => Right(UserResponse.fromUserEntity(user))
      case None => Left(InvalidRequest("Can not create user."))
    }
  }

  def users: List[UserResponse] = {
    UserRepository.getAllUser.map(UserResponse.fromUserEntity)
  }

  def user(loginId: String): Either[InvalidRequest, UserResponse] = {
    UserRepository.getUser(loginId) match {
      case Some(user) => Right(UserResponse.fromUserEntity(user))
      case None => Left(InvalidRequest("User Not Found."))
    }
  }

  def updateUser(loginId: String, user: UserRequest, signinUser: User): Either[InvalidRequest, UserResponse] = {
    if (signinUser.admin || signinUser.login == loginId) {
      val newUserInfo = signinUser.copy(
        name = user.name.getOrElse(signinUser.name),
        accessToken = user.accessToken.getOrElse(signinUser.accessToken),
        passwordHash = user.password
          .map(AuthenticationService.passwordHash)
          .getOrElse(signinUser.passwordHash)
      )
      UserRepository.updateUser(newUserInfo)
      Right(UserResponse.fromUserEntity(newUserInfo))
    } else {
      Left(InvalidRequest("Permission denied."))
    }
  }

  def updateUserAvatar(loginId: String, image: Array[Byte], signinUser: User): Either[InvalidRequest, UserResponse] = {
    if (signinUser.admin || signinUser.login == loginId) {
      val avatar = new AvatarService(signinUser, image)
      avatar.save match {
        case Right(_) =>
          val newUserInfo = signinUser.copy(avatarUri = avatar.serveUri)
          UserRepository.updateUser(newUserInfo)
          Right(UserResponse.fromUserEntity(newUserInfo))
        case Left(_) => Left(InvalidRequest(""))
      }
    } else {
      Left(InvalidRequest("Permission denied."))
    }
  }

  def validateUserInfo(user: CreateUserRequest): Boolean =
    user.login != "" && user.password != "" && user.name != ""
}
