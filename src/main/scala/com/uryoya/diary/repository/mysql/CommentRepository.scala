package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql._

object CommentRepository {
  /**
    * Comment IDからコメントを取得する.
    */
  def getComment(id: CommentId): Option[Comment] = {
    sql"""
      SELECT `id`, `author_id`, `diary_id`, `body`, `create_at`, `last_update_at`
      FROM `comments`
      WHERE `id` = $id
    """
      .query[Comment]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 任意の日報につけられたコメントすべてを取得する
    */
  def getAllComment(diary: Diary): List[Comment] = {
    sql"""
      SELECT `id`, `author_id`, `diary_id`, `body`, `create_at`, `last_update_at`
      FROM `comments`
      WHERE `diary_id` = ${diary.id}
    """
      .query[Comment]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 日報にコメントを追加する.
    */
  def addComment(diary: Diary, author: User, body: String): Int = {
    sql"""
      INSERT INTO `comments` (`author_id`, `diary_id`, `body`)
      VALUES (${author.id}, ${diary.id}, $body)
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * コメントを更新する.
    */
  def updateDiary(comment: Comment): Int = {
    sql"""
      UPDATE `comments`
      SET `body` = ${comment.body}
      WHERE `id` = ${comment.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
