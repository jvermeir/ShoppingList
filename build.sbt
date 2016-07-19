import AssemblyKeys._

assemblySettings

version := "1.3"

mainClass in assembly := Some("shop.ShoppingList")

name := "shoppinglist"

scalaVersion  := "2.11.8"

scalacOptions ++= Seq ( "-deprecation", "-feature" )

libraryDependencies ++= {
  Seq (
"org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "junit"     % "junit"      % "4.8.2"       % "test",
    "joda-time"      % "joda-time"       % "2.9.4"    ,
    "org.joda"         % "joda-convert"      % "1.8"     ,
    "org.apache.commons" % "commons-pool2" % "2.4.2" ,
    "commons-io"       % "commons-io"        % "2.5"      ,
    "org.slf4j" % "slf4j-api" % "1.7.21",
    "org.slf4j"    % "slf4j-log4j12" % "1.7.2"       % "provided" ,
    "log4j"          % "log4j"          % "1.2.16"  % "provided"
  )
}

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
