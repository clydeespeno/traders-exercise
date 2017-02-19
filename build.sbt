name := "api"

version := "1.0"

scalaVersion := "2.11.8"

val playVersion = "2.5.9"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % playVersion,
  "com.typesafe.play" %% "play-json" % playVersion
)