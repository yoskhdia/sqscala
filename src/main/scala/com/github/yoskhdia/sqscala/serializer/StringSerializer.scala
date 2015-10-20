package com.github.yoskhdia.sqscala.serializer

object StringSerializer extends MessageSerializer[String] {
  override def serialize(obj: String): MessageBody = obj

  override def deserialize(messageBody: MessageBody): String = messageBody
}
