
enablePlugins(ScalaJSPlugin)

enablePlugins(WorkbenchPlugin)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.coderunner",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "xmas-adventure",
    libraryDependencies += "org.typelevel" %%% "cats-core" % "1.5.0-RC1",
    libraryDependencies += "org.typelevel" %%% "cats-effect" % "1.0.0", 
    libraryDependencies += "org.querki" %%% "jquery-facade" % "1.2",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.2",
    libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.7",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
    scalacOptions += "-Ypartial-unification",
    scalaJSUseMainModuleInitializer := true,
    skip in packageJSDependencies := false,
    jsDependencies += "org.webjars" % "jquery" % "3.2.1" / "jquery.js" minified "jquery.min.js",
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv() //Allow working with the DOM in Node.js
  )
