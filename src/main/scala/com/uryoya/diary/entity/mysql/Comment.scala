package com.uryoya.diary.entity.mysql

import java.sql.Timestamp
import com.uryoya.diary.entity.{CommentId, UserId, DiaryId}

case class Comment (
  id: CommentId,
  authorId: UserId,
  diaryId: DiaryId,
  body: String,
  createAt: Timestamp,
  updateAt: Timestamp,
)
