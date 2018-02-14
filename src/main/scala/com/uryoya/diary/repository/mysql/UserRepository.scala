package com.uryoya.diary.repository.mysql

import doobie.implicits._
import com.uryoya.diary.entity._
import com.uryoya.diary.entity.mysql.User

object UserRepository {
  /**
    * User IDからUserを取得する.
    */
  def getUser(id: UserId): Option[User] = {
    sql"""
      SELECT `id`, `login`, `password_hash`, `name`, `avatar_uri`, `access_token`, `admin`
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
  def getUser(login: String): Option[User] = {
    sql"""
      SELECT `id`, `login`, `password_hash`, `name`, `avatar_uri`, `access_token`, `admin`
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
  def getAllUser: List[User] = {
    sql"""
      SELECT `id`, `login`, `password_hash`, `name`, `avatar_uri`, `access_token`, `admin`
      FROM `users`
    """
      .query[User]
      .list
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 既存のユーザか？
    */
  def exists(login: String): Boolean = {
    val user = sql"""
      SELECT `id`, `login`, `password_hash`, `name`, `avatar_uri`, `access_token`, `admin`
      FROM `users`
      WHERE `login` = ${login}
    """
      .query[User]
      .option
      .transact(MysqlTransactors.master)
      .unsafeRunSync
    user match {
      case Some(_) => true
      case None => false
    }
  }

  /**
    * ユーザを追加する
    *
    * TODO: ID重複などSQLでエラーが発生した場合、例外が創出されるので対策が必要
    */
  def addUser(login: String, passwordHash: String, name: String, avatarUri: String, accessToken: String, admin: Boolean): Int = {
    sql"""
      INSERT INTO `users` (`login`, `password_hash`, `name`, `avatar_uri`, `access_token`, `admin`)
      VALUES (${login}, ${passwordHash}, ${name}, ${avatarUri}, ${accessToken}, ${admin})
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
  def updateUser(user: User): Int = {
    sql"""
      UPDATE `users`
      SET `login` = ${user.login}, `password_hash` = ${user.passwordHash}, `name` = ${user.name}, `avatar_uri` = ${user.avatarUri}, `access_token` = ${user.accessToken}
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * 権限を昇格させる.
    */
  def promotionUser(user: User): Int = {
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
  def demoteUser(user: User): Int = {
    sql"""
      UPDATE `users`
      SET `admin` = 0
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }

  /**
    * ユーザを削除する
    */
  def deleteUser(user: User): Int = {
    sql"""
      DELETE FROM `users`
      WHERE `id` = ${user.id}
    """
      .update.run
      .transact(MysqlTransactors.master)
      .unsafeRunSync
  }
}
