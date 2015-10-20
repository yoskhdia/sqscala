package com.github.yoskhdia

import java.nio.ByteBuffer

import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.github.yoskhdia.sqscala.serializer.StringSerializer

package object sqscala {

  implicit val stringToMessageAttributeValue =
    (s: String) => new MessageAttributeValue().withDataType("String").withStringValue(s)
  implicit val intToMessageAttributeValue =
    (i: Int) => new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
  implicit val shortToMessageAttributeValue =
    (i: Short) => new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
  implicit val longToMessageAttributeValue =
    (i: Long) => new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
  implicit val floatToMessageAttributeValue =
    (i: Float) => new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
  implicit val doubleToMessageAttributeValue =
    (i: Double) => new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
  implicit val binaryToMessageAttributeValue =
    (b: Array[Byte]) => new MessageAttributeValue().withDataType("Binary").withBinaryValue(ByteBuffer.wrap(b))
  implicit val byteBufferToMessageAttributeValue =
    (b: ByteBuffer) => new MessageAttributeValue().withDataType("Binary").withBinaryValue(b)

  implicit val messageAttributeValueToString =
    (v: MessageAttributeValue) => v.getStringValue
  implicit val messageAttributeValueToInt =
    (v: MessageAttributeValue) => v.getStringValue.toInt
  implicit val messageAttributeValueToShort =
    (v: MessageAttributeValue) => v.getStringValue.toShort
  implicit val messageAttributeValueToLong =
    (v: MessageAttributeValue) => v.getStringValue.toLong
  implicit val messageAttributeValueToFloat =
    (v: MessageAttributeValue) => v.getStringValue.toFloat
  implicit val messageAttributeValueToDouble =
    (v: MessageAttributeValue) => v.getStringValue.toDouble
  implicit val messageAttributeValueToBinary =
    (v: MessageAttributeValue) => v.getBinaryValue.array()
  implicit val messageAttributeValueToByteBuffer =
    (v: MessageAttributeValue) => v.getBinaryValue


  // NOT implemented in AWS
  //implicit val stringListToMessageAttributeValue =
  //  (sl: Iterable[String]) => new MessageAttributeValue().withDataType("String").withStringListValues(sl.asJavaCollection)
  //implicit val intListToMessageAttributeValue =
  //  (il: Iterable[Int]) => new MessageAttributeValue().withDataType("Number").withStringListValues(il.map(_.toString).asJavaCollection)
  //implicit val shortListToMessageAttributeValue =
  //  (il: Iterable[Short]) => new MessageAttributeValue().withDataType("Number").withStringListValues(il.map(_.toString).asJavaCollection)
  //implicit val longListToMessageAttributeValue =
  //  (il: Iterable[Long]) => new MessageAttributeValue().withDataType("Number").withStringListValues(il.map(_.toString).asJavaCollection)
  //implicit val floatListToMessageAttributeValue =
  //  (il: Iterable[Float]) => new MessageAttributeValue().withDataType("Number").withStringListValues(il.map(_.toString).asJavaCollection)
  //implicit val doubleListToMessageAttributeValue =
  //  (il: Iterable[Double]) => new MessageAttributeValue().withDataType("Number").withStringListValues(il.map(_.toString).asJavaCollection)
  //implicit val binaryListToMessageAttributeValue =
  //  (b: Iterable[Array[Byte]]) => new MessageAttributeValue().withDataType("Binary").withBinaryListValues(b.map(x => ByteBuffer.wrap(x)).asJavaCollection)
  //implicit val byteBufferListToMessageAttributeValue =
  //  (b: Iterable[ByteBuffer]) => new MessageAttributeValue().withDataType("Binary").withBinaryListValues(b.asJavaCollection)


  object Implicits {
    implicit val stringSerializer = StringSerializer
  }

}
