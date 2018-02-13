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

  def updateUser(loginId: String, user: UserRequest, session: SessionService): Either[InvalidRequest, UserResponse] = {
    val signinUserId = session.get("login").getOrElse("")
    UserRepository.getUser(loginId).map { oldUserInfo =>
      if (oldUserInfo.admin || oldUserInfo.login == signinUserId) {
        val newUserInfo = oldUserInfo.copy(
          name = user.name.getOrElse(oldUserInfo.name),
          accessToken = user.accessToken.getOrElse(oldUserInfo.accessToken),
          passwordHash = user.password
            .map(AuthenticationService.passwordHash)
            .getOrElse(oldUserInfo.passwordHash)
        )
        UserRepository.updateUser(newUserInfo)
        Right(UserResponse.fromUserEntity(newUserInfo))
      } else {
        Left(InvalidRequest("Permission denied."))
      }
    }.getOrElse(Left(InvalidRequest("Permission denied.")))
  }

  def updateUserAvatar(loginId: String, image: Array[Byte], session: SessionService): Either[InvalidRequest, UserResponse] = {
    val signinUserId = session.get("login").getOrElse("")
    UserRepository.getUser(loginId).map { oldUserInfo =>
      if (oldUserInfo.admin || oldUserInfo.login == signinUserId) {
        val avatar = new AvatarService(oldUserInfo, image)
        avatar.save match {
          case Right(_) =>
            val newUserInfo = oldUserInfo.copy(avatarUri = avatar.serveUri)
            UserRepository.updateUser(newUserInfo)
            Right(UserResponse.fromUserEntity(newUserInfo))
          case Left(_) => Left(InvalidRequest(""))
        }
      } else {
        Left(InvalidRequest("Permission denied."))
      }
    }.getOrElse(Left(InvalidRequest("Permission denied.")))
  }

  def validateUserInfo(user: CreateUserRequest): Boolean =
    user.login != "" && user.password != "" && user.name != ""
}
