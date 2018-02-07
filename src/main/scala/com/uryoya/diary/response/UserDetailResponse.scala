package com.uryoya.diary.response

import com.uryoya.diary.entity.UserId

final case class UserDetailResponse(
  id: UserId,
  login: String,
  name: String,
  admin: Boolean,
)
