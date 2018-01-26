package com.uryoya.diary.entity.mysql

import java.sql.Timestamp
import com.uryoya.diary.entity.{DiaryId, UserId}

case class Diary (
  id: DiaryId,
  authorId: UserId,
  title: String,
  body: String,
  createAt: Timestamp,
  lastUpdateAt: Timestamp,
)
