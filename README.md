# sqscala
simple AWS SQS client for Scala.

This library, has been inspired by [com.kifi.franz](https://github.com/kifi/franz).
I like franz, but there are only a few of the configurations in order to create the AWS client.
I wanted a more customizable interfaces.

## Installation

```
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies += "com.github.yoskhdia" % "sqscala" % "1.0.2"
```

## How to use

### 1a. prepare by application.conf(easy way)

put application.conf in your classpath(resources).

```conf
# endpoint-url or region
# If you want to connect to localhost, example 'endpoint-url="http://localhost:9324"'
# endpoint-url = null
region = "us-west-2"

# max-retry = 3
# max-connections = 50
# connection-timeout-ms = 50000  # millis
# socket-timeout-ms = 50000      # millis
```

then your code, like

```scala
import com.github.yoskhdia.sqscala._

val client = ConfiguredSqsClient()
```

of course, you can wrap with container(e.g. `aws.sqs { ... }`), then use `ConfiguredSqsClient("aws.sqs")`.


### 1b. prepare by code

You can use SqsClient with credential provider and regions.
In many cases, it is easy that you should use regions pattern.
Of course, you can instantiate AWS client directly, and set SqsClient object to first argument.

```scala
import com.amazonaws.regions.Regions
import com.github.yoskhdia.sqscala._

val client = SqsClient()
// or
val client = SqsClient(regions = Regions.US_WEST_2)
// or
val client = SqsClient(credentialProvider = new YourCustomCredentialProvider())
```

### 2. connect the SQS via queue

Get a queue object from SqsClient.

```scala
val queue = client.queue(QueueName("foo"))
// or
val queue = client.queue(QueueName("foo"), createIfNotExists = true)
```

SqsQueue requires MessageSerializer used to serialize/deserialize the SQS message body.
(Message Body <=> Any Type)
By default, sqscala has only a StringSerializer(Message Body <=> String).
You can switch it on implicit parameter.

```scala
import com.github.yoskhdia.sqscala.Implicits.stringSerializer
// or
implicit val serializer = YourOriginalSerializer
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

1. support batch request.
2. support queue attributes.
3. support JSON serialization.
4. support stream access.
