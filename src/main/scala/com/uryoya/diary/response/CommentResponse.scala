package com.uryoya.diary.response

import com.uryoya.diary.entity.mysql._

case class CommentResponse (
  id: Int,
  diaryId: Int,
  author: UserResponse,
  body: String,
  createAt: java.sql.Timestamp,
  lastUpdateAt: java.sql.Timestamp,
)

object CommentResponse {
  def fromCommentEntity(comment: Comment, author: AboutUser) =
    CommentResponse(
      comment.id,
      comment.diaryId,
      UserResponse.fromUserEntity(author),
      comment.body,
      comment.createAt,
      comment.lastUpdateAt,
    )
}
