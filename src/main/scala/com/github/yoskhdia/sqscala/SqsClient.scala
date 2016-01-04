package com.github.yoskhdia.sqscala

import com.amazonaws.auth.{AWSCredentialsProvider, DefaultAWSCredentialsProviderChain}
import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.{AmazonSQSAsync, AmazonSQSAsyncClient}

import scala.concurrent.{Future, Promise}

trait SqsClient {

  import SqsClient._

  def awsClient: AmazonSQSAsync

  protected def getQueueUrl(queueName: QueueName, createIfNotExists: Boolean): QueueUrl = {
    val request = new GetQueueUrlRequest(queueName.simplified)
    QueueUrl(
      if (createIfNotExists) {
        synchronized {
          try {
            awsClient.getQueueUrl(request).getQueueUrl
          } catch {
            case e: QueueDoesNotExistException =>
              awsClient.createQueue(new CreateQueueRequest(queueName.simplified)).getQueueUrl
            case e: Throwable => throw e
          }
        }
      } else {
        // skip synchronized block if you don't need create queue
        awsClient.getQueueUrl(request).getQueueUrl
      }
    )
  }

  def queue(queueName: QueueName, createIfNotExists: Boolean = false): SqsQueue = {
    new SqsQueueImpl(queueName, getQueueUrl(queueName, createIfNotExists), this)
  }

  def deleteQueue(queueName: QueueName): Future[Unit] = {
    val request = new DeleteQueueRequest()
      .withQueueUrl(getQueueUrl(queueName, createIfNotExists = false).simplified)

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
    * apply SQS client.
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
    val awsClient = createAwsClient(credentialProvider, regions)
    apply(awsClient)
  }

  /**
    * apply SQS client.
    *
    * @param awsClient AWS SQS client.
    * @return SQS client
    */
  def apply(awsClient: AmazonSQSAsync): SqsClient = {
    new SqsClientImpl(awsClient)
  }

  /**
    * apply SQS client that has unsafe operation methods.
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
  def unsafe(credentialProvider: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
             regions: Regions = Regions.DEFAULT_REGION): SqsClient with UnsafeOps = {
    val awsClient = createAwsClient(credentialProvider, regions)
    unsafe(awsClient)
  }

  /**
    * apply SQS client that has unsafe operation methods.
    *
    * @param awsClient AWS SQS client.
    * @return SQS client
    */
  def unsafe(awsClient: AmazonSQSAsync): SqsClient with UnsafeOps = {
    new SqsClientImpl(awsClient) with UnsafeOps
  }

  private def createAwsClient(credentialProvider: AWSCredentialsProvider, regions: Regions) = {
    val awsClient = new AmazonSQSAsyncClient(credentialProvider)
    awsClient.setRegion(Region.getRegion(regions))
    awsClient
  }

  private class SqsClientImpl(val awsClient: AmazonSQSAsync) extends SqsClient

  private class SqsQueueImpl(val name: QueueName, val queueUrl: QueueUrl, protected val client: SqsClient) extends SqsQueue

  trait UnsafeOps {
    self: SqsClient =>

    /**
      * get SQS queue.
      * this method doesn't check queue exist.
      *
      * @param queueName queue name.
      * @param queueUrl queue url.
      * @return SQS queue
      */
    def queue(queueName: QueueName, queueUrl: String): SqsQueue = {
      val url = QueueUrl(queueUrl, verifyFormat = true).fold(t => throw t, u => u)
      queue(queueName, url)
    }

    /**
      * get SQS queue.
      * this method doesn't check queue exist.
      *
      * @param queueName queue name.
      * @param queueUrl queue url.
      * @return SQS queue
      */
    def queue(queueName: QueueName, queueUrl: QueueUrl): SqsQueue = {
      new SqsQueueImpl(queueName, queueUrl, this)
    }

  }

}
