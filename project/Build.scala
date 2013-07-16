import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "songwich-api"
  val appVersion      = "0.4"

  val appDependencies = Seq(
    javaCore, 
    javaJdbc, javaEbean,
    
    // Add your project dependencies here,
    //"postgresql" % "postgresql" % "8.4-702.jdbc4"
    //"leodagdag" %% "play2-morphia-plugin" % "0.0.14"
    "com.google.code.morphia" % "morphia" % "0.99.1-SNAPSHOT"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    //resolvers += "LeoDagDag repository" at "http://leodagdag.github.com/repository/",
    resolvers += "Morphia repo" at "http://morphia.googlecode.com/svn/mavenrepo/",
    checksums := Nil
  )
}
