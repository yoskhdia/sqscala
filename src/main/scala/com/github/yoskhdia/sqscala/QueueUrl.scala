package com.github.yoskhdia.sqscala

import java.net.URL

case class QueueUrl(simplified: String) {

  def toUrl: URL = {
    new URL(simplified)
  }
}

object QueueUrl {

  def apply(simplified: String, verifyFormat: Boolean): Either[Throwable, QueueUrl] = {
    try {
      new URL(simplified)
      Right(QueueUrl(simplified))
    } catch {
      case t: Throwable => Left(t)
    }
  }
}
