organization := "com.github.jeanadrien"
name := "evrythng-scala-sdk"

scalaVersion := "2.11.8"

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
        id    = "jeanadrien",
        name  = "Jean-Adrien Vaucher",
        email = "jean@jeanjean.ch",
        url   = url("https://github.com/jeanadrien")
    )
)

// publish setup
publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

// Dependencies
libraryDependencies += "io.spray" %% "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

// Http client adapters. Provided.
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.14" % "provided"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5" % "provided"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2" % "provided"

// Test
libraryDependencies += "org.specs2" %% "specs2-core" % "3.8.5" % "test"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7" % "test"
libraryDependencies += "org.apache.httpcomponents" % "httpcore" % "4.4.5" % "test"
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.2" % "test"
libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.14" % "test"

// build settings
pomIncludeRepository := { _ => false }
publishMavenStyle := true

lazy val root = project.in(file("."))
    .settings(releaseProcess := ReleaseProcess.process)
