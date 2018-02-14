package com.uryoya.diary.controller

import com.uryoya.diary.entity.{CommentId, InvalidRequest}
import com.uryoya.diary.entity.mysql.{Comment, User}
import com.uryoya.diary.repository.mysql.{CommentRepository, DiaryRepository}
import com.uryoya.diary.request._
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

  def updateComment(commentId: CommentId, dstCommentReq: CommentRequest, signinUser: User):
  Either[InvalidRequest, MessageResponse] = {
    val maybeDstComment = for {
      (srcComment, author) <- CommentRepository.getComment(commentId)
      if author.id == signinUser.id
    } yield srcComment.copy(body=dstCommentReq.body)
    maybeDstComment match {
      case Some(dstComment) =>
        CommentRepository.updateComment(dstComment)
        Right(MessageResponse("Success."))
      case None => Left(InvalidRequest(""))
    }
  }
}
