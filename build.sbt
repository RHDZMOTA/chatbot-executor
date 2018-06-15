import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.rhdzmota",
      scalaVersion := "2.12.6",
      version      := "0.1.0"
    )),
    name := "chatbot-executor",
    libraryDependencies ++= {
      val circeVersion = "0.9.3"
      val configVersion = "1.3.1"
      val akkaHttpVersion = "10.1.1"
      val akkaVersion = "2.5.12"
      Seq(
        "com.typesafe" % "config" % configVersion,
        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
        "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub" % "0.19",
        "org.mongodb.scala" %% "mongo-scala-driver" % "2.3.0",
        "io.netty" % "netty-all" % "4.1.17.Final",
        "org.mongodb" % "bson" % "2.3",
        "com.rhdzmota" %% "pubsub-scala" % "1.0.0",
        "com.rhdzmota" %% "fb-messenger" % "1.0.0",
        scalaTest % Test
      )
    }
  )
