package com.github.yoskhdia.sqscala

import com.amazonaws.services.sqs.model.MessageAttributeValue

case class Message[T](messageId: MessageId,
                      body: T,
                      receiptHandle: ReceiptHandle,
                      attributes: Map[String, String] = Map.empty[String, String],
                      messageAttributes: Map[String, MessageAttributeValue] = Map.empty[String, MessageAttributeValue])
