organization := "com.github.yoskhdia"

licenses +=("MIT", url("http://opensource.org/licenses/MIT"))

name := "sqscala"

version := "1.0.6"

scalaVersion := "2.11.7"

scalacOptions in GlobalScope in Compile := Seq(
  "-unchecked", "-deprecation", "-feature",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen"
)
scalacOptions in Test ++= Seq("-Yrangepos")

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

val specs2Version = "3.8.+"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-sqs" % "1.11.+",
  "com.typesafe" % "config" % "1.3.+",
  "org.specs2" %% "specs2-core" % specs2Version % "test",
  "org.specs2" %% "specs2-mock" % specs2Version % "test"
)

// Test Options

elasticMQVersion := "0.10.1"
nodeAddressConf := NodeAddressConf(port = 9325)
restSQSConf := RestSQSConf(bindPort = 9325)

startElasticMQ <<= startElasticMQ.dependsOn(compile in Test)
test in Test <<= (test in Test).dependsOn(startElasticMQ)
testOptions in Test <+= elasticMQTestCleanup
testOnly in Test <<= (testOnly in Test).dependsOn(startElasticMQ)
