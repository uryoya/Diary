package com.uryoya.diary.entity.mysql

import com.uryoya.diary.entity.UserId

case class AboutUser (
  id: UserId,
  login: String,
  name: String,
  avatarUri: String,
  admin: Boolean,
)
