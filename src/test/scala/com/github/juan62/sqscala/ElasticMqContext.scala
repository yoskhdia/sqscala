package com.github.juan62.sqscala

import org.elasticmq.rest.sqs.SQSRestServerBuilder

trait ElasticMqContext {
  ElasticMqContext.server
}

object ElasticMqContext {
  // TODO: I know that should stop server. But now ElasticMQ server is started in JVM process, so it will be stopped after specification.
  lazy val server = synchronized {
    SQSRestServerBuilder.withPort(9325).withInterface("localhost").start()
  }
}