
enablePlugins(ScalaJSPlugin)

enablePlugins(WorkbenchPlugin)

enablePlugins(DockerPlugin)

val monocleVersion = "1.5.0"

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
    libraryDependencies += "org.scala-lang.modules" %%% "scala-parser-combinators" % "1.1.1",
    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-core"  % monocleVersion,
    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-macro" % monocleVersion,
    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-law"   % monocleVersion % Test,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
    scalacOptions += "-Ypartial-unification",
    scalaJSUseMainModuleInitializer := true,
    skip in packageJSDependencies := false,
    jsDependencies += "org.webjars" % "jquery" % "3.2.1" / "jquery.js" minified "jquery.min.js",
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(), //Allow working with the DOM in Node.js
    dockerfile in docker := {
      val jsDir  = target.value / "scala-2.12"
      val resDir  = target.value / "scala-2.12" / "classes"
      val projectDir = project.base.getAbsolutePath
    
      val dockerFile = new Dockerfile {
          from("nginx:alpine")
            .maintainer("Tim Bennett")
            .copy(jsDir / "xmas-adventure-opt.js", "/usr/share/nginx/html")
            .copy(jsDir / "xmas-adventure-jsdeps.min.js", "/usr/share/nginx/html")
            .copy(resDir / "index.html", "/usr/share/nginx/html")
            .copy(resDir / "computer-hum.wav", "/usr/share/nginx/html")
            .copy(resDir / "MerryXmas.mp3", "/usr/share/nginx/html")
            .copy(resDir / "pc.png", "/usr/share/nginx/html")
            .copy(resDir / "styles.css", "/usr/share/nginx/html")
            .copy(resDir / "success.wav", "/usr/share/nginx/html")
      }
    
      dockerFile
    },
    imageNames in docker := Seq(
      // Sets the latest tag
      ImageName(
          namespace = Some("bennetimo"),
          repository = name.value,
          tag = Some("latest")
      )
    )
  )
