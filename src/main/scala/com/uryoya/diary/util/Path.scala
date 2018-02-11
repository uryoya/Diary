package com.uryoya.diary.util

object Path {
  def join(pp: String*): String = pp.toSeq.flatMap(_.split("/")).mkString("/")
}
