package com.uryoya.diary.entity.mysql

import com.uryoya.diary.entity.{EventId, DiaryId, CommentId}

case class Event (
  id: EventId,
  eventType: String,  // diary.sql では`type`だがScalaと名前空間がかぶるので`eventType`
  diaryId: Option[DiaryId],
  commentId: Option[CommentId],
)
