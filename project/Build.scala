import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "songwich-api"
  val appVersion = "0.4"

  val appDependencies = Seq(
    javaCore,

    // Add your project dependencies here,
    "org.mongodb" % "mongo-java-driver" % "2.11.4",
    //"org.mongodb" % "mongo-java-driver" % "2.11.2",
    "com.google.code.morphia" % "morphia" % "0.104",
    //"com.google.code.morphia" % "morphia" % "0.101.0",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.104",
    //"com.google.code.morphia" % "morphia-validation" % "0.101.0"
    "org.slf4j" % "slf4j-api" % "1.7.2", // versioning for Play 2.1.3
    "commons-validator" % "commons-validator" % "1.4.0",
    "com.google.inject" % "guice" % "3.0",
    "org.apache.commons" % "commons-lang3" % "3.1" //"com.fasterxml.jackson.core" % "jackson-core" % "2.2.2",
    //"com.fasterxml.jackson.core" % "jackson-databind" % "2.2.2",
    //"com.fasterxml.jackson.core" % "jackson-annotations" % "2.2.2"
    )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += "MongoDB repo" at "https://github.com/mongodb/mongo-java-driver/",
    resolvers += "Morphia repo" at "http://morphia.googlecode.com/svn/mavenrepo/",
    checksums := Nil,

    testOptions in Test ~= { args =>
      for {
        arg <- args
        val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
        val newArg = if (ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
      } yield newArg
    })
}
