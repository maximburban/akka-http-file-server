import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

lazy val akkaHttpVersion = "10.2.7"
lazy val akkaVersion = "2.6.18"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.13.8"
    )),
    name := "akka-http-file-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.9",

      "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "org.scalatest" %% "scalatest" % "3.2.9" % Test,
      "org.mockito" %% "mockito-scala-scalatest" % "1.17.0" % Test
    )
  )
enablePlugins(DockerPlugin, JavaAppPackaging)

Docker / packageName := "akka-http-file-server"
dockerExposedPorts := Seq(8080)
dockerBaseImage := "anapsix/alpine-java"

dockerCommands ++= Seq(
  ExecCmd("WORKDIR", "files"),
)