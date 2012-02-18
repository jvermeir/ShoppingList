import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "ShoppingListWeb"
    val appVersion      = "1.1"

    val appDependencies = Seq(
  "wuzuhwuzuh" % "shop" % "1.1",
  "commons-io" % "commons-io" % "2.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies).settings(defaultScalaSettings:_*).settings(
      // Add your own project settings here      
    )

}
            
