import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "songwich-api"
  val appVersion      = "0.4"

  val appDependencies = Seq(
    javaCore,
    
    // Add your project dependencies here,
    "org.mongodb" % "mongo-java-driver" % "2.11.2",
    "com.google.code.morphia" % "morphia" % "0.101.0",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.101.0"
    //"com.google.code.morphia" % "morphia-validation" % "0.101.0"
    
    /*
    "com.fasterxml.jackson.core" % "jackson-core" % "2.2.2",
    "com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2",
    "com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.2"
    */
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "MongoDB repo" at "https://github.com/mongodb/mongo-java-driver/",
    resolvers += "Morphia repo" at "http://morphia.googlecode.com/svn/mavenrepo/",
    checksums := Nil
  )
}
