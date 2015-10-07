package com.github.juan62.sqscala

import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import org.specs2.specification.core.Env

import scala.util.Try

class SqsClientOperations(specEnv: Env) extends Specification with ElasticMqContext with BeforeAfterAll {
  implicit val ee = specEnv.executionEnv
  implicit val ec = specEnv.executionContext

  var client: SqsClient = _

  override def beforeAll(): Unit = client = ConfiguredSqsClient()

  override def afterAll(): Unit = client.shutdown()

  "create and get queue then delete" in {
    val queueName = QueueName("test")
    Try(client.queue(queueName, createIfNotExists = true)) must beSuccessfulTry
    client.deleteQueue(queueName) must be_==(()).await
  }

}
