resolvers ++= Seq(
    DefaultMavenRepository,
    Resolver.url("Play", url("http://download.playframework.org/ivy-releases/"))(Resolver.ivyStylePatterns),
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Local Maven Repository" at "file://~/.m2/repository"
)

libraryDependencies += "play" %% "play" % "2.0-beta"