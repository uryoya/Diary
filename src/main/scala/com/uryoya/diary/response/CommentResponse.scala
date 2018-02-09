package com.uryoya.diary.response

case class CommentResponse (
  id: Int,
  diaryId: Int,
  author: UserResponse,
  body: String,
  createAt: java.sql.Timestamp,
  lastUpdateAt: java.sql.Timestamp,
)
