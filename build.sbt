import AssemblyKeys._

assemblySettings

version := "1.2"

mainClass in assembly := Some("shop.ShoppingList")

name := "shoppinglist"

scalaVersion  := "2.11.2"

scalacOptions ++= Seq ( "-deprecation", "-feature" )

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq (
"org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "junit"     % "junit"      % "4.8.2"       % "test",
    "joda-time"      % "joda-time"       % "2.0"    ,
    "org.joda"         % "joda-convert"      % "1.1"     ,
    "redisclient"        % "redisclient"         % "2.10-2.10",
    "commons-pool"   % "commons-pool"    % "1.6"     ,
    "commons-io"       % "commons-io"        % "2.4"      ,
    "org.slf4j"             % "slf4j-api"              % "1.7.2"      ,
    "org.slf4j"    % "slf4j-log4j12" % "1.7.2"       % "provided" ,
    "log4j"          % "log4j"          % "1.2.16"  % "provided",
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray" %%  "spray-json" % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
