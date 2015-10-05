package sqscala.serializer

trait MessageSerializer[T] {

  def serialize(obj: T): String

  def deserialize(messageBody: String): T
}
