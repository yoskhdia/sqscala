package com.github.juan62.sqscala.serializer

object StringSerializer extends MessageSerializer[String] {
  override def serialize(obj: String): String = obj

  override def deserialize(messageBody: String): String = messageBody
}
