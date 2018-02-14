package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.UserRepository
import com.uryoya.diary.request.{CreateUserRequest, UserRequest}
import com.uryoya.diary.response.{MessageResponse, UserResponse}
import com.uryoya.diary.service.{AuthenticationService, AvatarService, SessionService}
import shapeless.HNil

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

  def updateMyself(dstUserReq: UserRequest, signinUser: User): Either[InvalidRequest, MessageResponse] = {
    val newUserInfo = signinUser.copy(
      name = dstUserReq.name.getOrElse(signinUser.name),
      accessToken = dstUserReq.accessToken.getOrElse(signinUser.accessToken),
      passwordHash = dstUserReq.password
        .map(AuthenticationService.passwordHash)
        .getOrElse(signinUser.passwordHash)
    )
    if (1 == UserRepository.updateUser(newUserInfo))
      Right(MessageResponse("Success."))
    else
      Left(InvalidRequest("Update failed."))
  }

  def updateMyselfAvatar(image: Array[Byte], signinUser: User): Either[InvalidRequest, MessageResponse] = {
    val avatar = new AvatarService(signinUser, image)
    avatar.save match {
      case Right(_) =>
        UserRepository.updateUser(signinUser.copy(avatarUri = avatar.serveUri))
        Right(MessageResponse("Success."))
      case Left(_) => Left(InvalidRequest("Update failed."))
    }
  }

  def deleteUser(loginId: String, signinUser: User): Either[InvalidRequest, MessageResponse] = {
    if (signinUser.admin || signinUser.login == loginId) {
      UserRepository.getUser(loginId)
        .map(UserRepository.deleteUser)
      Right(MessageResponse("Success."))
    } else {
      Left(InvalidRequest("Permission denied."))
    }
  }

  def validateUserInfo(user: CreateUserRequest): Boolean =
    user.login != "" && user.password != "" && user.name != ""
}
