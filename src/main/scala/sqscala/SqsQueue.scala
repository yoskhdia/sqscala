package sqscala

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.services.sqs.model._
import sqscala.serializer.MessageSerializer

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps

trait SqsQueue {

  def name: QueueName

  protected def queueUrl: String

  protected def client: SqsClient

  def receive[T](waitTime: Option[FiniteDuration] = None, visibilityTimeout: Option[FiniteDuration] = None)(implicit serializer: MessageSerializer[T]): Future[Option[Message[T]]] = {
    val request = new ReceiveMessageRequest()
      .withQueueUrl(queueUrl)
      .withMaxNumberOfMessages(1)
    waitTime.foreach(d => request.withWaitTimeSeconds(d.toSeconds.toInt))
    visibilityTimeout.foreach(d => request.withVisibilityTimeout(d.toSeconds.toInt))

    val p = Promise[Option[Message[T]]]()
    client.awsClient.receiveMessageAsync(request, new AsyncHandler[ReceiveMessageRequest, ReceiveMessageResult] {
      override def onError(exception: Exception): Unit = p.failure(exception)

      override def onSuccess(request: ReceiveMessageRequest, result: ReceiveMessageResult): Unit = {
        result.getMessages.asScala.headOption.fold(p.success(None)) { message =>
          p.success(Some(
            Message(
              MessageId(message.getMessageId),
              serializer.deserialize(message.getBody),
              ReceiptHandle(message.getReceiptHandle),
              message.getAttributes.asScala.toMap,
              message.getMessageAttributes.asScala.toMap
            )
          ))
        }
      }
    })
    p.future
  }

  def send[T](message: T, delay: Option[FiniteDuration] = None, messageAttributes: Map[String, MessageAttributeValue] = Map.empty[String, MessageAttributeValue])(implicit serializer: MessageSerializer[T]): Future[MessageId] = {
    val request = new SendMessageRequest()
      .withQueueUrl(queueUrl)
      .withMessageBody(serializer.serialize(message))
    messageAttributes.foreach { case (key, value) =>
      request.addMessageAttributesEntry(key, value)
    }
    delay.foreach(d => request.withDelaySeconds(d.toSeconds.toInt))

    val p = Promise[MessageId]()
    client.awsClient.sendMessageAsync(request, new AsyncHandler[SendMessageRequest, SendMessageResult] {
      override def onError(exception: Exception): Unit = p.failure(exception)

      override def onSuccess(request: SendMessageRequest, result: SendMessageResult): Unit = p.success(MessageId(result.getMessageId))
    })
    p.future
  }

  def delete(receiptHandle: ReceiptHandle): Future[Unit] = {
    val request = new DeleteMessageRequest()
      .withQueueUrl(queueUrl)
      .withReceiptHandle(receiptHandle.value)

    val p = Promise[Unit]()
    client.awsClient.deleteMessageAsync(request, new AsyncHandler[DeleteMessageRequest, Void] {
      override def onError(exception: Exception): Unit = p.failure(exception)

      override def onSuccess(request: DeleteMessageRequest, result: Void): Unit = p.success(Unit)
    })
    p.future
  }
}
