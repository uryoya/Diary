package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql._

object DiaryRepository {
  /**
    * Diary IDから日報を取得する.
    */
  def getDiary(id: DiaryId): Option[(Diary, AboutUser)] = {
    sql"""
      SELECT
       d.id, d.author_id, d.title, d.body, d.create_at, d.last_update_at,
       u.id, u.login, u.name, u.avatar_uri, u.admin
      FROM diaries AS d
      LEFT JOIN users as u
      ON d.author_id = u.id
      WHERE d.id = $id
    """
      .query[(Diary, AboutUser)]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * すべての日報を取得する.
    */
  def getAllDiary: List[(Diary, AboutUser)] = {
    sql"""
      SELECT
       d.id, d.author_id, d.title, d.body, d.create_at, d.last_update_at,
       u.id, u.login, u.name, u.avatar_uri, u.admin
      FROM diaries AS d
      LEFT JOIN users as u
      ON d.author_id = u.id
    """
      .query[(Diary, AboutUser)]
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
    val updateTime = java.sql.Timestamp.from(java.time.Instant.now)
    sql"""
      UPDATE `diaries`
      SET `title` = ${diary.title}, `body` = ${diary.body}, `last_update_at` = $updateTime
      WHERE `id` = ${diary.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
