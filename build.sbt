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
    publishTo := Some("GitHub Packages".at("https://maven.pkg.github.com/ZERDICORP/ez-xlsx")),
    credentials += Credentials(
      "GitHub Packages",
      "maven.pkg.github.com",
      "ZERDICORP",
      sys.env.getOrElse("GITHUB_TOKEN", "")
    )
  )
