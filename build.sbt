ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "ez-xlsx",
    libraryDependencies ++= Seq(
      "org.apache.logging.log4j" %% "log4j-api-scala" % "13.1.0",
      "org.apache.logging.log4j" % "log4j-core" % "2.23.1",
      "org.apache.poi" % "poi-ooxml" % "5.3.0",
      "org.typelevel" %% "cats-core" % "2.13.0"
    ),
    organization := "com.nanikin",
    credentials += Credentials(
      "Reposilite",
      "repo.nanikin.ru",
      "admin",
      sys.env("REPOSILITE_TOKEN")
    ),
    publishTo := Some("Reposilite".at("https://repo.nanikin.ru/releases"))
  )
