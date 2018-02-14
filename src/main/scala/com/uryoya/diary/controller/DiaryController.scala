package com.uryoya.diary.controller

import com.uryoya.diary.entity.{DiaryId, InvalidRequest}
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.DiaryRepository
import com.uryoya.diary.request._
import com.uryoya.diary.response._

object DiaryController {
  def postDiary(signinUser: User, newDiary: PostDiaryRequest): Either[InvalidRequest, MessageResponse] = {
    if (1 == DiaryRepository.addDiary(signinUser, newDiary.title, newDiary.body))
      Right(MessageResponse("Success."))
    else
      Left(InvalidRequest("failed add to db."))
  }

  def diaries: List[DiaryResponse] =
    DiaryRepository.getAllDiary map {
      case (diary, author) => DiaryResponse.fromDiaryEntity(diary, author)
    }

  def diary(diaryId: DiaryId): Option[DiaryResponse] =
    DiaryRepository.getDiary(diaryId) map {
      case (diary, author) => DiaryResponse.fromDiaryEntity(diary, author)
    }

  def updateDiary(diaryId: DiaryId, dstDiaryReq: DiaryRequest, signinUser: User):
  Either[InvalidRequest, MessageResponse] = {
    val maybeDstDiary = for {
      (srcDiary, _) <- DiaryRepository.getDiary(diaryId)
      if srcDiary.authorId == signinUser.id
    } yield srcDiary.copy(
      title=dstDiaryReq.title.getOrElse(srcDiary.title),
      body=dstDiaryReq.body.getOrElse(srcDiary.body)
    )
    maybeDstDiary match {
      case Some(dstDiary) =>
        DiaryRepository.updateDiary(dstDiary)
        Right(MessageResponse("Success."))
      case None => Left(InvalidRequest(""))
    }
  }
}
