package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql.User

object UserRepository {
  /**
    * User IDからUserを取得する.
    */
  def get(id: UserId): Option[User] = {
    sql"""
      SELECT `id`, `login`, `name`, `avatar_uri`, `access_token`, `admin`
      FROM `users`
      WHERE `id` = $id
    """
      .query[User]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * login IDからUserを取得する.
    */
  def get(login: String): Option[User] = {
    sql"""
      SELECT `id`, `login`, `name`, `avatar_uri`, `access_token`, `admin`
      FROM `users`
      WHERE `login` = $login
    """
      .query[User]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * すべてのユーザを取得する.
    */
  def getAll: List[User] = {
    sql"""
      SELECT `id`, `login`, `name`, `avatar_uri`, `access_token`, `admin`
      FROM `users`
    """
      .query[User]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * ユーザを追加する
    *
    * TODO: ID重複などSQLでエラーが発生した場合、例外が創出されるので対策が必要
    */
  def add(user: User): Int = {
    sql"""
      INSERT INTO `users` (`id`, `login`, `name`, `avatar_uri`, `access_token`, `admin`)
      VALUES (${user.id}, ${user.login}, ${user.name}, ${user.avatarUri}, ${user.accessToken}, ${user.admin})
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * ユーザ情報を更新する.
    *
    * 更新できるのは login, name, avatar_uri, access_token のみ
    */
  def update(user: User): Int = {
    sql"""
      UPDATE `users`
      SET `login` = ${user.login}, `name` = ${user.name}, `avatar_uri` = ${user.avatarUri}, `access_token` = ${user.accessToken}
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 権限を昇格させる.
    */
  def promotion(user: User): Int = {
    sql"""
      UPDATE `users`
      SET `admin` = 1
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 権限を降格させる.
    */
  def demote(user: User): Int = {
    sql"""
      UPDATE `users`
      SET `admin` = 0
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
