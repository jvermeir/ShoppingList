name := "shoppinglist"

version := "1.1"

scalaVersion := "2.10.0"

resolvers ++= Seq ("Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
                   "Spray repo" at "http://repo.spray.io"
                   )

scalacOptions ++= Seq ( "-deprecation", "-feature" )

libraryDependencies ++= Seq (
			"org.scalatest"    % "scalatest_2.10"    % "1.9.1"       ,
			"junit"     % "junit"      % "4.8.2"       % "test",
			"joda-time"      % "joda-time"       % "2.0"    ,
            "org.joda"         % "joda-convert"      % "1.1"     ,
            "redisclient"        % "redisclient"         % "2.10-2.10",
            "commons-pool"   % "commons-pool"    % "1.6"     ,
            "commons-io"       % "commons-io"        % "2.4"      ,
            "com.typesafe.akka"   % "akka-actor_2.10"      % "2.1.0"     ,
            "org.slf4j"             % "slf4j-api"              % "1.7.2"      ,
            "org.slf4j"    % "slf4j-log4j12" % "1.7.2"       % "provided" ,
            "log4j"          % "log4j"          % "1.2.16"  % "provided",
            "io.spray" % "spray-io" % "1.1-M7",
            "io.spray" % "spray-can" % "1.1-M7"
)
