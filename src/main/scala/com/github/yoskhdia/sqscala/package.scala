package com.github.yoskhdia

import java.nio.ByteBuffer

import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.github.yoskhdia.sqscala.serializer.StringSerializer

package object sqscala {

  implicit class StringToMessageAttributeValue(val s: String) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("String").withStringValue(s)
    }
  }

  implicit class IntToMessageAttributeValue(val i: Int) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
    }
  }

  implicit class ShortToMessageAttributeValue(val i: Short) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
    }
  }

  implicit class LongToMessageAttributeValue(val i: Long) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
    }
  }

  implicit class FloatToMessageAttributeValue(val i: Float) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
    }
  }

  implicit class DoubleToMessageAttributeValue(val i: Double) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Number").withStringValue(i.toString)
    }
  }

  implicit class BinaryToMessageAttributeValue(val b: Array[Byte]) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Binary").withBinaryValue(ByteBuffer.wrap(b))
    }
  }

  implicit class ByteBufferToMessageAttributeValue(val b: ByteBuffer) extends AnyVal {
    def toMessage: MessageAttributeValue = {
      new MessageAttributeValue().withDataType("Binary").withBinaryValue(b)
    }
  }


  implicit class MessageAttributeValueToScalaObject(val v: MessageAttributeValue) extends AnyVal {
    private def validate[T](dataType: String)(f: MessageAttributeValue => T): T = {
      if (v.getDataType == dataType) {
        f(v)
      } else {
        throw new Exception
      }
    }

    // String Data Type

    def toStringValue: String = validate("String") { mv =>
      mv.getStringValue
    }

    // Number Data Type

    private def numberValue: String = validate("Number") { mv =>
      val s = mv.getStringValue
      if (s == null) {
        throw new Exception("For the Number data type, you must use StringValue.")
      }
      s
    }

    def toInt: Int = numberValue.toInt

    def toShort: Short = numberValue.toShort

    def toLong: Long = numberValue.toLong

    def toFloat: Float = numberValue.toFloat

    def toDouble: Double = numberValue.toDouble

    // Binary Data Type

    def toBinary: Array[Byte] = validate("Binary") { mv =>
      val b = mv.getBinaryValue
      if (b == null) {
        return Array.emptyByteArray
      }
      b.array()
    }

    def toByteBuffer: ByteBuffer = validate("Binary") { mv =>
      mv.getBinaryValue
    }
  }


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
