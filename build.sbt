import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.coderunner",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "xmas-adventures",
    libraryDependencies += cats,
    libraryDependencies += catsEffect,
    libraryDependencies += scalaTest % Test,
    libraryDependencies += scalaCheck % Test,
    scalacOptions += "-Ypartial-unification"
  )
