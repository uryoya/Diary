package com.uryoya.diary.request

final case class UserRequest (
  name: Option[String],
  accessToken: Option[String],
  password: Option[String],
)
