package com.uryoya.diary.service

import java.io.{FileOutputStream, FileWriter}

import com.twitter.util.Future
import com.twitter.io.Writer
import com.uryoya.diary.config
import com.uryoya.diary.util.Path

final class AvatarService(image: Array[Byte]) {
  lazy val fileName: String = genFilename
  lazy val serveUri: String = Path.join(config.userAvatar.serverRootPath, fileName)
  lazy val localUri: String = Path.join(config.userAvatar.localRootPath, fileName)

  def save: Option[Unit] = {
    try {
      val imgFile = new java.io.File(localUri)
      val imgWriter = new FileOutputStream(imgFile)
      imgWriter.write(image)
      Some(Unit)
    } catch {
      case _:Exception => None
    }
  }

  def genFilename: String = {
    val md5 = java.security.MessageDigest.getInstance("MD5")
    md5.digest(image).mkString + ".png"
  }
}
