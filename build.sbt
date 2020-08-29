import sbt.Append.appendSeq

version := "1.7-snapshot"

mainClass in assembly := Some("shop.Main")

organization := "com.example"
name := "shoppinglist"

scalaVersion := "2.13.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")

val ScalatraVersion = "2.7.0"
resolvers += Classpaths.typesafeReleases
lazy val akkaHttpVersion = "10.1.12"
lazy val akkaVersion    = "2.6.8"

libraryDependencies ++=
  Seq(
    "joda-time" % "joda-time" % "2.9.4",
    "org.joda" % "joda-convert" % "1.8",
    "org.apache.commons" % "commons-pool2" % "2.4.2",
    "commons-io" % "commons-io" % "2.5",
    "org.slf4j" % "slf4j-api" % "1.7.21",
    "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "provided",
    "log4j" % "log4j" % "1.2.16" % "provided",

    "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
    "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
    "ch.qos.logback"    % "logback-classic"           % "1.2.3",

    "org.scalatest" %% "scalatest" % "3.2.0" % Test,
    "junit" % "junit" % "4.12" % Test,
    "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test
  )

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

Test / parallelExecution := false