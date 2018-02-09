package com.uryoya.diary.response

final case class DiaryResponse (
  id: Int,
  author: UserResponse,
  title: String,
  body: String,
  createAt: java.sql.Timestamp,
  lastUpdateAt: java.sql.Timestamp,
  comments: List[CommentResponse],
)
