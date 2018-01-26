package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql._

object DiaryRepository {
  /**
    * Diary IDから日報を取得する.
    */
  def getDiary(id: DiaryId): Option[Diary] = {
    sql"""
      SELECT `id`, `author_id`, `title`, `body`, `create_at`, `last_update_at`
      FROM `diaries`
      WHERE `id` = $id
    """
      .query[Diary]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * すべての日報を取得する.
    */
  def getAllDiary: List[Diary] = {
    sql"""
      SELECT `id`, `author_id`, `title`, `body`, `create_at`, `last_update_at`
      FROM `diaries`
    """
      .query[Diary]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * ユーザが作成した日報をすべて取得する.
    */
  def getAllDiaryWrittenByUser(author: User): List[Diary] = {
    sql"""
      SELECT `id`, `author_id`, `title`, `body`, `create_at`, `last_update_at`
      FROM `diaries`
      WHERE `author_id` = ${author.id}
    """
      .query[Diary]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 日報を追加する.
    */
  def addDiary(author: User, title: String, body: String): Int = {
    sql"""
      INSERT INTO `diaries` (`author_id`, `title`, `body`)
      VALUES (${author.id}, $title, $body)
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 日報を更新する.
    */
  def updateDiary(diary: Diary): Int = {
    sql"""
      UPDATE `diaries`
      SET `title` = ${diary.title}, `body` = ${diary.body}
      WHERE `id` = ${diary.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
