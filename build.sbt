import sbt.Append.appendSeq

version := "1.6"

mainClass in assembly := Some("shop.Main")

organization := "com.example"
name := "shoppinglist"

scalaVersion := "2.13.1"

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")

val ScalatraVersion = "2.7.0"
resolvers += Classpaths.typesafeReleases

libraryDependencies ++=
  Seq(
    "org.scalatest" %% "scalatest" % "3.2.0" % "test",
    "org.scalactic" %% "scalactic" % "3.2.0",
    "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
    "junit" % "junit" % "4.12" % "test",

    "joda-time" % "joda-time" % "2.9.4",
    "org.joda" % "joda-convert" % "1.8",
    "org.apache.commons" % "commons-pool2" % "2.4.2",
    "commons-io" % "commons-io" % "2.5",
    "org.slf4j" % "slf4j-api" % "1.7.21",
    "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "provided",
    "log4j" % "log4j" % "1.2.16" % "provided",
    "org.scalatra" %% "scalatra" % ScalatraVersion,
    "org.scalatra" %% "scalatra-json" % ScalatraVersion,
    "org.json4s" %% "json4s-jackson" % "3.6.9",
    "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
    "org.eclipse.jetty" % "jetty-webapp" % "9.4.28.v20200408" % "container",
    "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
  )

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

Test / parallelExecution := false