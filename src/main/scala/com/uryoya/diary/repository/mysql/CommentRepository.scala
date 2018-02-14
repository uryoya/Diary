package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql._

object CommentRepository {
  /**
    * Comment IDからコメントを取得する.
    */
  def getComment(id: CommentId): Option[(Comment, AboutUser)] = {
    sql"""
      SELECT
        c.id, c.author_id, c.diary_id, c.body, c.create_at, c.last_update_at,
        u.id, u.login, u.name, u.avatar_uri, u.admin
      FROM comments as c
      LEFT JOIN users as u
      ON c.author_id = u.id
      WHERE c.id = $id
    """
      .query[(Comment, AboutUser)]
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
      .transact(MysqlTransactors.master) .unsafeRunSync
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
  def updateComment(comment: Comment): Int = {
    val updateTime = java.sql.Timestamp.from(java.time.Instant.now)
    sql"""
      UPDATE `comments`
      SET `body` = ${comment.body}, `last_update_at` = $updateTime
      WHERE `id` = ${comment.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * コメントを削除する.
    */
  def deleteComment(comment: Comment): Int = {
    sql"""
      DELETE FROM `comments`
      WHERE `id` = ${comment.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
