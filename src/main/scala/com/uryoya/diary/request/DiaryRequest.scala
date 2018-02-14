package com.uryoya.diary.request

final case class DiaryRequest (
  title: Option[String],
  body: Option[String],
)
