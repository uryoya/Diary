package com.uryoya.diary.request

final case class CreateUserRequest (
  login: String,
  name: String,
  accessToken: String,
  password: String,
)
