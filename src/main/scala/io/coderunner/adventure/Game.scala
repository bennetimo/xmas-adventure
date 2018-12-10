package io.coderunner.adventure

import cats.effect.IO
import org.querki.jquery._
import org.scalajs.dom.ext.KeyCode

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object Game {

  val input = $("#userInput")
  val audio = $("#audio")

  val document = js.Dynamic.global.document
  val audioDom = document.getElementById("audio")

  val enterPressed =
    new Channel[JQueryEventObject](input.keyup(_), e => e.which == KeyCode.Enter)

  def getLine: IO[String] = IO.fromFuture(IO(enterPressed())).flatMap(_ => readInput)

  val clearInput: IO[Unit] = IO(input.value(""))

  val playSound: IO[Unit] = IO{
    audio.attr("src", "MerryXmas.mp3")
    audioDom.play()
  }

  val stopSound: IO[Unit] = IO(audio.attr("src", ""))

  val readInput: IO[String] = IO(input.valueString)

  def putLine(s: String, tagType: String = "p"): IO[Unit] = IO($("#output").append(s"<$tagType>$s</$tagType>"))

  def scroll(): IO[Unit] = IO {
    val screen = org.scalajs.dom.document.getElementById("output")
    screen.scrollTop = screen.scrollHeight.toDouble
  }

  def gameLoop(): IO[Unit] = {
    for {
      _ <- scroll
      _ <- putLine("What do you want to do?")
      input <- getLine
      _ <- clearInput
      _ <- input match {
        case "quit" => putLine("OK, see you again soon!")
        case "play" => playSound.flatMap(_ => gameLoop)
        case "stop" => stopSound.flatMap(_ => gameLoop)
        case x => putLine(s"OK, let's $x!").flatMap(_ => gameLoop)
      }
    } yield ()
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    (for {
      _ <- putLine(Ascii.logo, "pre")
      _ <- gameLoop()
    }yield ()).unsafeRunAsyncAndForget()
  }
}

class Channel[T](init: (T => Unit) => Unit, cond: T => Boolean){
  init(update)
  private[this] var value: Promise[T] = null
  def apply(): Future[T] = {
    value = Promise[T]()
    value.future
  }
  def update(t: T): Unit = {
    if (value != null && !value.isCompleted && cond(t)) value.success(t)
  }
}