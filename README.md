# sqscala
simple AWS SQS client for Scala

## How to use

### 1a. prepare by application.conf(easy way)

put application.conf in your classpath(resources).

```conf
aws {
  sqs {
    # endpoint-url or region
    # If you want to connect to localhost, example 'endpoint-url="http://localhost:9324"'
    # endpoint-url = null
    region = "us-west-2"

    # max-retry = 3
    # max-connections = 50
    # connection-timeout-ms = 50000  # millis
    # socket-timeout-ms = 50000      # millis
  }
}
```

then your code, like

```scala
import com.github.juan62.sqscala._

val client = ConfiguredSqsClient()
```

### 1b. prepare by code

You can use SqsClient with credential provider and regions.
Of course, you can instantiate AWS client directly, then put into SqsClient first argument.
In many cases, you should only use regions pattern.

```scala
import com.amazonaws.regions.Regions
import com.github.juan62.sqscala._

val client = SqsClient()
// or
val client = SqsClient(regions = Regions.US_WEST_2)
// or
val client = SqsClient(credentialProvider = new YourCustomCredentialProvider())
```

### 2. connect SQS via queue

Get queue object from SqsClient.

```scala
val queue = client.queue(QueueName("foo"))
// or
val queue = client.queue(QueueName("foo"), createIfNotExists = true)
```

SqsQueue needs MessageSerializer.
MessageSerializer is used at serializing/deserializing SQS message body.
(Message Body[String] <=> Any Type)

```scala
import com.github.juan62.sqscala.Implicits.stringSerializer
```

then you can use queue, like

```scala
// send message to SQS
queue.send("hello, sqs.").onComplete {
  // ...
}

// receive message from SQS
queue.receive().onComplete {
  case Success(message) => // ...
  case Failure(exception) => // ...
}

// delete message from SQS
queue.receive().onSuccess {
  case Some(message) =>
    // ...
    queue.delete(message.receiptHandle).onComplete {
      // ...
    }
}
```

## Road map

1. write test.
2. support batch request.
3. support queue attributes.
4. support JSON serialization.

## Contribute

I know my english is childish...
please help me with your knowledge.
