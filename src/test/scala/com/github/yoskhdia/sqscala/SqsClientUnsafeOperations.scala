package com.github.yoskhdia.sqscala

import com.github.yoskhdia.sqscala.SqsClient.UnsafeOps
import org.specs2.concurrent.ExecutionEnv
import org.specs2.mutable.Specification
import org.specs2.specification.BeforeAfterAll
import org.specs2.specification.core.Env

class SqsClientUnsafeOperations(specEnv: Env) extends Specification with BeforeAfterAll {
  private[this] var client: SqsClient with UnsafeOps = _

  override def beforeAll(): Unit = client = ConfiguredSqsClient("aws.sqs").unsafe

  override def afterAll(): Unit = client.shutdown()

  val unit = ()

  "create queue by client.queue method" in { implicit ee: ExecutionEnv =>
    val queueName = QueueName("test4")
    val queue = client.queue(queueName, createIfNotExists = true)
    client.queue(queueName, queue.url).url must_=== queue.url
    client.queue(queueName, "http://localhost:9325/queue/test4").url must_=== queue.url
    client.deleteQueue(queueName) must be_==(unit).await
  }
}
