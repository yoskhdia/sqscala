package com.github.yoskhdia.sqscala

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

class SqsQueueTest extends Specification with ElasticMqContext with BeforeAfterAll {
  private[this] var client: SqsClient = _
  private[this] lazy val queue: SqsQueue = client.queue(QueueName("test"), createIfNotExists = true)

  override def beforeAll(): Unit = client = ConfiguredSqsClient("aws.sqs")

  override def afterAll(): Unit = client.shutdown()

  "SqsQueueTest" should {
    "send -> receive -> delete message as string" in { implicit ee: ExecutionEnv =>
      import Implicits.stringSerializer

      val message = "hello, sqs"

      // send
      queue.send(message).map(Option(_)) must beSome[MessageId].await
      // receive
      val re = Await.result(queue.receive(), 20 seconds)
      re must beSome.like {
        case m =>
          m.body must be_==(message)
          // delete
          queue.delete(m.receiptHandle) must be_==(()).await
      }
      // check queue is empty
      queue.receive() must beNone.await
    }
  }
}
