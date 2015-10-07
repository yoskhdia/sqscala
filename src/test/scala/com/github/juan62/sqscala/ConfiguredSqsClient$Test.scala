package com.github.juan62.sqscala

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.Specification

import scala.util.Try

class ConfiguredSqsClient$Test extends Specification {

  "ConfiguredSqsClient$Test" should {
    "legal configuration" >> {
      val configString =
        """
          |aws {
          |  sqs {
          |    endpoint-url = "http://localhost:9324"
          |
          |    max-retry = 1
          |    max-connections = 10
          |    connection-timeout-ms = 30000
          |    socket-timeout-ms = 30000
          |  }
          |}
        """.stripMargin
      val config = ConfigFactory.parseString(configString)

      "apply by config object" in {
        Try(ConfiguredSqsClient(config)) must beSuccessfulTry
      }

      "apply by config file" in {
        Try(ConfiguredSqsClient()) must beSuccessfulTry
      }
    }

    "illegal configuration" >> {
      "configuration need either endpoint or region" in {
        val configA = ConfigFactory.parseString(
          """
            |aws.sqs {
            |  endpoint-url = "http://localhost:9324"
            |  region = "ap-northeast-1"
            |}
          """.stripMargin
        )
        Try(ConfiguredSqsClient(configA)) must beFailedTry

        val configB = ConfigFactory.parseString(
          """
            |aws.sqs {
            |  # nothing
            |}
          """.stripMargin
        )
        Try(ConfiguredSqsClient(configB)) must beFailedTry
      }

    }
  }
}
