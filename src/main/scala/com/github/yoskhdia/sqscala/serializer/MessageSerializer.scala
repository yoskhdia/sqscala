package com.github.yoskhdia.sqscala.serializer

trait MessageSerializer[T] {
  type MessageBody = String

  def serialize(obj: T): MessageBody

  def deserialize(messageBody: MessageBody): T
}
