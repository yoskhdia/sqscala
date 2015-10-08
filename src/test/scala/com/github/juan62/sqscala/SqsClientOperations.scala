package com.github.juan62.sqscala

import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import org.specs2.specification.core.Env

import scala.util.Try

class SqsClientOperations(specEnv: Env) extends Specification with ElasticMqContext with BeforeAfterAll {
  var client: SqsClient = _

  override def beforeAll(): Unit = client = ConfiguredSqsClient()

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
}
