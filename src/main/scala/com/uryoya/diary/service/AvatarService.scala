package com.uryoya.diary.service

import java.awt.image.{AreaAveragingScaleFilter, BufferedImage, FilteredImageSource}
import java.awt.Toolkit
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

import com.uryoya.diary.config
import com.uryoya.diary.entity.mysql.User
import com.uryoya.diary.util.Path

final class AvatarService(user: User, image: Array[Byte]) {
  lazy val edgeSize: Int = config.userAvatar.edgeSize
  lazy val fileName: String = genFilename
  lazy val serveUri: String = Path.join(config.userAvatar.serverRootPath, fileName)
  lazy val localUri: String = Path.join(config.userAvatar.localRootPath, fileName)

  def save: Either[Exception, Boolean] = {
    try {
      val srcBufImg = ImageIO.read(new ByteArrayInputStream(image))
      val filter = new AreaAveragingScaleFilter(edgeSize, edgeSize)
      val dstImg = Toolkit.getDefaultToolkit.createImage(
        new FilteredImageSource(srcBufImg.getSource, filter)
      )
      val dstBufImg = new BufferedImage(
        dstImg.getWidth(null),
        dstImg.getHeight(null),
        BufferedImage.TYPE_INT_RGB,
      )
      val g = dstBufImg.createGraphics()
      g.drawImage(dstImg, 0, 0, null)
      g.dispose()
      Right(ImageIO.write(dstBufImg, "jpg", new java.io.File(localUri)))
    } catch {
      case e:Exception => Left(e)
    }
  }

  private def genFilename: String = {
    s"${user.login}_avatar.${config.userAvatar.format}"
  }
}
