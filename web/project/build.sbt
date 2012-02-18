resolvers += Classpaths.typesafeSnapshots

resolvers += Classpaths.typesafeResolver

resolvers += ( "Local Maven Repository" at "file://~/.m2/repository")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.5.0")
