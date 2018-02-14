package com.uryoya.diary.response

import com.uryoya.diary.entity.mysql._
import com.uryoya.diary.repository.mysql.CommentRepository

final case class DiaryResponse (
  id: Int,
  author: UserResponse,
  title: String,
  body: String,
  createAt: java.sql.Timestamp,
  lastUpdateAt: java.sql.Timestamp,
  comments: List[CommentResponse],
)

object DiaryResponse {
  def fromDiaryEntity(diary: Diary, author: AboutUser): DiaryResponse =
    DiaryResponse(
      diary.id,
      UserResponse.fromUserEntity(author),
      diary.title,
      diary.body,
      diary.createAt,
      diary.lastUpdateAt,
      CommentRepository
        .getAllComment(diary)
        .map(CommentResponse.fromCommentEntity(_, author))
    )
}
