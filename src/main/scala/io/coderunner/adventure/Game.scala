package io.coderunner.adventure

import cats.effect.IO
import org.querki.jquery._
import org.scalajs.dom.ext.KeyCode

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.annotation.JSExport

object Game {

  val input = $("#userInput")

  val enterPressed =
    new Channel[JQueryEventObject](input.keyup(_), e => e.which == KeyCode.Enter)

  def getLine: IO[String] = IO.fromFuture(IO(enterPressed())).flatMap(_ => readInput)

  val clearInput: IO[Unit] = IO(input.value(""))

  val readInput: IO[String] = IO(input.valueString)

  def putLine(s: String): IO[Unit] = IO($("#output").append(s"<p>$s</p>"))

  def gameLoop(): IO[Unit] = {
    for {
      _ <- putLine("What do you want to do?")
      input <- getLine
      _ <- clearInput
      _ <- input match {
        case "quit" => putLine("OK, see you again soon!")
        case x => putLine(s"OK, let's $x!").flatMap(_ => gameLoop)
      }
    } yield ()
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    gameLoop().unsafeRunAsyncAndForget()
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