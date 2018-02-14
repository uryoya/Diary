package com.uryoya.diary.request

import com.uryoya.diary.entity.DiaryId

case class PostCommentRequest (
  diaryId: DiaryId,
  body: String,
)
