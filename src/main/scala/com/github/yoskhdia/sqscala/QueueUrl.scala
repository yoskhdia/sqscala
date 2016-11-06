package com.github.yoskhdia.sqscala

import java.net.URL

import scala.util.Try

case class QueueUrl(simplified: String) {

  def toUrl: URL = {
    new URL(simplified)
  }
}

object QueueUrl {

  def parse(simplified: String, verifyFormat: Boolean): Try[QueueUrl] = {
    Try {
      new URL(simplified)
      QueueUrl(simplified)
    }
  }
}
