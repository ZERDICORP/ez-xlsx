ThisBuild / version := "2.5.0"
ThisBuild / versionScheme := Some("semver-spec")
ThisBuild / scalaVersion := "2.11.5"

lazy val root = (project in file("."))
  .settings(
    name := "ez-xlsx",
    publish := {}
  )
  .aggregate(`ez-xlsx-core`, `ez-xlsx-apache-poi`)

lazy val `ez-xlsx-core` = (project in file("modules/core"))
  .settings(
    name := "ez-xlsx-core",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.0.0",
      "org.scalatest" %% "scalatest-funsuite" % "3.2.19" % "test"
    ),
    organization := "com.nanikin",
    credentials += Credentials(
      "Reposilite",
      "repo.nanikin.ru",
      "admin",
      sys.env.getOrElse("REPO_TOKEN", "n/a")
    ),
    publishTo := Some("Reposilite".at("https://repo.nanikin.ru/releases"))
  )

lazy val `ez-xlsx-apache-poi` = (project in file("modules/apache-poi"))
  .settings(
    name := "ez-xlsx-apache-poi",
    libraryDependencies ++= Seq(
      "org.apache.logging.log4j" %% "log4j-api-scala" % "13.1.0",
      "org.apache.logging.log4j" % "log4j-core" % "2.23.1",
      "org.apache.poi" % "poi-ooxml" % "5.3.0",
      "org.scalatest" %% "scalatest-funsuite" % "3.2.19" % "test"
    ),
    organization := "com.nanikin",
    credentials += Credentials(
      "Reposilite",
      "repo.nanikin.ru",
      "admin",
      sys.env.getOrElse("REPO_TOKEN", "n/a")
    ),
    publishTo := Some("Reposilite".at("https://repo.nanikin.ru/releases"))
  )
  .dependsOn(`ez-xlsx-core`)

lazy val `example-apache-poi` = (project in file("examples/apache-poi"))
  .settings(
    name := "example-apache-poi",
    publish := {}
  )
  .dependsOn(`ez-xlsx-apache-poi`)
