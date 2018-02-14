package com.uryoya.diary.controller

import com.uryoya.diary.entity.{CommentId, InvalidRequest}
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.repository.mysql.{CommentRepository, DiaryRepository}
import com.uryoya.diary.request.PostCommentRequest
import com.uryoya.diary.response.{CommentResponse, MessageResponse}

object CommentController {
  def postComment(commentReq: PostCommentRequest, author: User): Either[InvalidRequest, MessageResponse] = {
    DiaryRepository.getDiary(commentReq.diaryId) map {
      case (diary, _) => CommentRepository.addComment(diary, author, commentReq.body)
    } match {
      case Some(1) => Right(MessageResponse("Success."))
      case Some(_) => Left(InvalidRequest("Post failed."))
      case None => Left(InvalidRequest("Not found."))
    }
  }

  def comment(commentId: CommentId): Option[CommentResponse] = {
    CommentRepository.getComment(commentId) map {
      case (c, u) => CommentResponse.fromCommentEntity(c, u)
    }
  }
}
