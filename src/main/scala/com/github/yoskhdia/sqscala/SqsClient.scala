package com.github.yoskhdia.sqscala

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.{AmazonSQSAsync, AmazonSQSAsyncClient}

import scala.concurrent.{Future, Promise}

trait SqsClient {

  def awsClient: AmazonSQSAsync

  protected def getQueueUrl(queueName: QueueName, createIfNotExists: Boolean): String = synchronized {
    try {
      val request = new GetQueueUrlRequest(queueName.simplified)
      awsClient.getQueueUrl(request).getQueueUrl
    } catch {
      case e: QueueDoesNotExistException if createIfNotExists =>
        awsClient.createQueue(new CreateQueueRequest(queueName.simplified)).getQueueUrl
      case e: Throwable => throw e
    }
  }

  def queue(queueName: QueueName, createIfNotExists: Boolean = false): SqsQueue = {
    val outer = this
    new SqsQueue {
      override val name: QueueName = queueName

      override protected val queueUrl: String = getQueueUrl(queueName, createIfNotExists)

      override protected val client: SqsClient = outer
    }
  }

  def deleteQueue(queueName: QueueName): Future[Unit] = {
    val request = new DeleteQueueRequest()
      .withQueueUrl(getQueueUrl(queueName, createIfNotExists = false))

    val p = Promise[Unit]()
    awsClient.deleteQueueAsync(request, new AsyncHandler[DeleteQueueRequest, Void] {
      override def onError(exception: Exception): Unit = p.failure(exception)

      override def onSuccess(request: DeleteQueueRequest, result: Void): Unit = p.success(Unit)
    })
    p.future
  }

  def createQueue(queueName: QueueName): Future[Unit] = {
    val request = new CreateQueueRequest()
      .withQueueName(queueName.simplified)

    val p = Promise[Unit]()
    awsClient.createQueueAsync(request, new AsyncHandler[CreateQueueRequest, CreateQueueResult] {
      override def onError(exception: Exception): Unit = p.failure(exception)

      override def onSuccess(request: CreateQueueRequest, result: CreateQueueResult): Unit = p.success(Unit)
    })
    p.future
  }

  def shutdown(): Unit = awsClient.shutdown()
}

object SqsClient {
  /**
   * apply sqs client.
   *
   * @param credentialProvider AWS credentials provider.
   *                           Default AWS credentials provider chain that looks for credentials in this order:
   *                           <ul>
   *                           <li>Environment Variables -
   *                           <code>AWS_ACCESS_KEY_ID</code> and <code>AWS_SECRET_ACCESS_KEY</code>
   *                           (RECOMMENDED since they are recognized by all the AWS SDKs and CLI except for .NET),
   *                           or <code>AWS_ACCESS_KEY</code> and <code>AWS_SECRET_KEY</code> (only recognized by Java SDK)
   *                           </li>
   *                           <li>Java System Properties - aws.accessKeyId and aws.secretKey</li>
   *                           <li>Credential profiles file at the default location (~/.aws/credentials) shared by all AWS SDKs and the AWS CLI</li>
   *                           <li>Instance profile credentials delivered through the Amazon EC2 metadata service</li>
   *                           </ul>
   * @param regions AWS regions.
   * @return SQS client
   */
  def apply(credentialProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
            regions: Regions = Regions.DEFAULT_REGION): SqsClient = {
    val awsClient = new AmazonSQSAsyncClient(credentialProvider)
    awsClient.setRegion(Region.getRegion(regions))
    apply(awsClient)
  }

  def apply(awsClient: AmazonSQSAsync): SqsClient = {
    val ac = awsClient
    new SqsClient {
      override val awsClient: AmazonSQSAsync = ac
    }
  }
}
