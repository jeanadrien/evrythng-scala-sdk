organization := "com.github.jeanadrien"
name := "evrythng-scala-sdk"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

pomIncludeRepository := { _ => false }

licenses := Seq(
    "Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
)

homepage := Some(url("https://github.com/jeanadrien/evrythng-scala-sdk"))

scmInfo := Some(
    ScmInfo(
        url("https://github.com/jeanadrien/evrythng-scala-sdk"),
        "scm:git@github.com:jeanadrien/evrythng-scala-sdk.git"
    )
)

developers := List(
    Developer(
        id = "jeanadrien",
        name = "Jean-Adrien Vaucher",
        email = "jean@jeanjean.ch",
        url = url("https://github.com/jeanadrien")
    )
)


// Dependencies
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.10"
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

// Http client adapters. Provided.
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.10" % "provided"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5" % "provided"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2" % "provided"

// Test
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % "test"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5" % "test"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2" % "test"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.10" % "test"

