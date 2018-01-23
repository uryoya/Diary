package com.uryoya.diary.entity.mysql

import com.uryoya.diary.entity.UserId

case class User (
  id: UserId,
  login: String,
  name: String,
  avatarUri: String,
  accessToken: String,
  admin: Boolean,
)
