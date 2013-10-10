import play.Project._

name := "songwich-api"

version := "0.4"

javacOptions += "-Xlint:deprecation"     

libraryDependencies ++= Seq(
    "org.mongodb" % "mongo-java-driver" % "2.11.2",
    "com.google.code.morphia" % "morphia" % "0.101.0",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.101.0"
)    

resolvers += "MongoDB repo" at "https://github.com/mongodb/mongo-java-driver/"

resolvers += "Morphia repo" at "http://morphia.googlecode.com/svn/mavenrepo/"

checksums := Nil

playJavaSettings
