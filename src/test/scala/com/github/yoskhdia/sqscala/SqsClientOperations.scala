package com.github.yoskhdia.sqscala

import com.amazonaws.services.sqs.model.QueueDoesNotExistException
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import org.specs2.specification.core.Env

import scala.util.Try

class SqsClientOperations(specEnv: Env) extends Specification with BeforeAfterAll {
  private[this] var client: SqsClient = _

  override def beforeAll(): Unit = client = ConfiguredSqsClient("aws.sqs")

  override def afterAll(): Unit = client.shutdown()

  val unit = ()

  "create queue by client.queue method" in { implicit ee: ExecutionEnv =>
    val queueName = QueueName("test1")
    Try(client.queue(queueName, createIfNotExists = true)) must beSuccessfulTry
    client.deleteQueue(queueName) must be_==(unit).await
  }

  "create queue by client.createQueue method" in { implicit ee: ExecutionEnv =>
    val queueName = QueueName("test2")
    client.createQueue(queueName) must be_==(unit).await
    client.deleteQueue(queueName) must be_==(unit).await
  }

  "get queue is failed when queue is not created yet and createIfNotExists = false" in { implicit ee: ExecutionEnv =>
    val queueName = QueueName("test3")
    client.queue(queueName, createIfNotExists = false) must throwA[QueueDoesNotExistException]
    client.queue(queueName) must throwA[QueueDoesNotExistException]
  }

}
