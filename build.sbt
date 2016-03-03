name := "jsonshootout"

organization := "com.despegar"

scalaVersion := "2.11.7"

resolvers += "spray" at "http://repo.spray.io/"

resolvers += "Mandubian repository snapshots" at "https://github.com/mandubian/mandubian-mvn/raw/master/snapshots/"

fork in run := true

libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"

libraryDependencies += "net.liftweb" %% "lift-json" % "3.0-M8"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.0-M2"

libraryDependencies += "org.json4s" %% "json4s-native" % "3.3.0"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.3.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.3.0",
  "io.circe" %% "circe-generic" % "0.3.0",
  "io.circe" %% "circe-parser" % "0.3.0",
  "io.circe" %% "circe-jackson" % "0.3.0"
)
