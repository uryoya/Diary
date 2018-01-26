package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql._

object EventRepository {
  /**
    * Event IDからイベントを取得する。
    */
  def getEvent(id: EventId): Option[Event] = {
    sql"""
      SELECT `id`, `type`, `diary_id`, `comment_id`
      FROM `events`
      WHERE `id` = $id
    """
      .query[Event]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * すべてのイベントを取得する。
    */
  def getAllEvent: List[Event] = {
    sql"""
      SELECT `id`, `type`, `diary_id`, `comment_id`
      FROM `events`
    """
      .query[Event]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 日報投稿イベントを追加する。
    */
  def addPostedDiaryEvent(diary: Diary): Int = {
    sql"""
      INSERT INTO `events` (`type`, `diary_id`)
      VALUES ('posted', ${diary.id})
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
  /**
    * 日報更新イベントを追加する。
    */
  def addUpdatedDiaryEvent(diary: Diary): Int = {
    sql"""
      INSERT INTO `events` (`type`, `diary_id`)
      VALUES ('updated', ${diary.id})
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
  /**
    * コメント投稿イベントを追加する。
    */
  def addPostedCommentEvent(comment: Comment): Int = {
    sql"""
      INSERT INTO `events` (`type`, `comment_id`)
      VALUES ('posted', ${comment.id})
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
  /**
    * コメント更新イベントを追加する。
    */
  def addUpdatedCommentEvent(comment: Comment): Int = {
    sql"""
      INSERT INTO `events` (`type`, `comment_id`)
      VALUES ('updated', ${comment.id})
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
