package sqscala

import com.amazonaws.ClientConfiguration
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.retry.PredefinedRetryPolicies
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.typesafe.config.{Config, ConfigFactory}
import sqscala.exception.ConfigurationNotFoundException

import scala.util.control.NonFatal

object ConfiguredSqsClient {
  def apply(): SqsClient = {
    apply(ConfigFactory.load())
  }

  def apply(sqsConfiguration: Config): SqsClient = {
    val sdkClient = new AmazonSQSAsyncClient(sdkConfig(sqsConfiguration))

    val region = sqsConfiguration.readStringOption("region")
    val endpoint = sqsConfiguration.readStringOption("endpoint-url")

    // region and endpoint are related. so if you set both parameters, AWS SQS client accept only after one.
    (region, endpoint) match {
      case (Some(_), Some(_)) => throw new ConfigurationNotFoundException("need either region or endpoint-url.")
      case (Some(r), None) => sdkClient.setRegion(Region.getRegion(Regions.fromName(r)))
      case (None, Some(e)) => sdkClient.setEndpoint(e)
      case _ => throw new ConfigurationNotFoundException("region or endpoint-url is not found.")
    }

    SqsClient(sdkClient)
  }

  private def sdkConfig(sqsConfiguration: ConfigWrapper): ClientConfiguration = {
    val sdkConfig = new ClientConfiguration()

    // number of max retry
    sdkConfig.setRetryPolicy(
      PredefinedRetryPolicies.getDefaultRetryPolicyWithCustomMaxRetries(
        sqsConfiguration.readIntOr("max-retry", PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY)
      ))
    // number of HTTP connections
    sdkConfig.setMaxConnections(
      sqsConfiguration.readIntOr("max-connections", ClientConfiguration.DEFAULT_MAX_CONNECTIONS))
    // timeout of connection creation
    sdkConfig.setConnectionTimeout(
      sqsConfiguration.readIntOr("connection-timeout-ms", ClientConfiguration.DEFAULT_CONNECTION_TIMEOUT))
    // timeout of socket
    sdkConfig.setSocketTimeout(
      sqsConfiguration.readIntOr("socket-timeout-ms", ClientConfiguration.DEFAULT_SOCKET_TIMEOUT))

    sdkConfig
  }

  private implicit class ConfigWrapper(val config: Config) extends AnyVal {
    def readStringOption(path: String): Option[String] = {
      wrapOption(path, config.getString(path))
    }

    def readStringOr(path: String, default: => String): String = {
      wrapOption(path, config.getString(path)).getOrElse(default)
    }

    def readIntOption(path: String): Option[Int] = {
      wrapOption(path, config.getInt(path))
    }

    def readIntOr(path: String, default: => Int): Int = {
      wrapOption(path, config.getInt(path)).getOrElse(default)
    }

    private def wrapOption[T](path: String, value: => T): Option[T] = {
      try {
        if (config.hasPathOrNull(path))
          Some(value)
        else
          None
      } catch {
        case NonFatal(e) => throw new ConfigurationNotFoundException(s"caught exception. path=$path", e)
      }
    }
  }

}
