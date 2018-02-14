package com.uryoya.diary.controller

import com.uryoya.diary.entity.InvalidRequest
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.DiaryRepository
import com.uryoya.diary.request.DiaryRequest
import com.uryoya.diary.response.MessageResponse

object DiaryController {
  def postDiary(signinUser: User, newDiary: DiaryRequest): Either[InvalidRequest, MessageResponse] = {
    if (1 == DiaryRepository.addDiary(signinUser, newDiary.title, newDiary.body))
      Right(MessageResponse("Success."))
    else
      Left(InvalidRequest("failed add to db."))
  }
}
