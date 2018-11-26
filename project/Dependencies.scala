import sbt._

object Dependencies {
  lazy val cats = "org.typelevel" %% "cats-core" % "1.5.0-RC1"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "1.0.0"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.14.0"
}
