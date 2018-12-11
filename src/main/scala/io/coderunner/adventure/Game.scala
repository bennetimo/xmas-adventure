package io.coderunner.adventure

import cats.effect.IO
import org.querki.jquery._
import org.scalajs.dom.ext.KeyCode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

object Game {

  val input = $("#userInput")
  val audio = $("#audio")

  // This has the effect of having the 'update' function of the Channel passed as the event handler, so we can
  // intercept the key press values being passed back. Each time we press a key, the handler will be called, which
  // checks the condition to see whether it was an Enter key. If so, the future is completed. This is used to
  // effectively allow us to simulate a blocking readLine input in the browser, reading from the text input. Only
  // when the player has fully entered a line does the game loop continue
  val enterPressed = new Channel[JQueryEventObject](input.keyup(_), e => e.which == KeyCode.Enter)

  def getLine: IO[String] = IO.fromFuture(IO(enterPressed())).flatMap(_ => readInput)

  val clearInput: IO[Unit] = IO(input.value(""))

  def playSound(file: String): IO[Unit] = IO{
    lazy val document = js.Dynamic.global.document
    lazy val audioDom = document.getElementById("audio")

    audio.attr("src", file)
    audioDom.play()
  }

  val stopSound: IO[Unit] = IO(audio.attr("src", ""))

  val readInput: IO[String] = IO(input.valueString)

  def putLine(s: String, tagType: String = "p"): IO[Unit] = IO($("#output").append(s"<$tagType>$s</$tagType>"))
  def prompt(s: String): IO[Unit] = IO($("#prompt").text(s"$s"))
  def putLineSlowly(s: String, cssClass: String, tagType: String = "p"): IO[Boolean] = {
    val words = s.split(" ").zipWithIndex

    def slowly(f: Boolean => Unit): Unit = {
      $("#output").append(s"""<$tagType class="$cssClass"></$tagType>""")

      words.foreach { case (s, i) =>
        js.timers.setTimeout(80 * i) {
          $(s"#output p.$cssClass:last").append(s"$s ")
          f(i >= words.length - 1)
        }
      }
    }

    val textFinished = new Channel[Boolean](slowly, _ == true)

    IO.fromFuture(IO(textFinished()))
  }

  def slowWords(s: String, finishedCallback: Boolean => Unit): Unit = {
    val words = s.split(" ").zipWithIndex
  }

  def scroll: IO[Unit] = IO {
    val screen = org.scalajs.dom.document.getElementById("output")
    screen.scrollTop = screen.scrollHeight.toDouble
  }

  def gameLoop(): IO[Unit] = {
    (for {
      _ <- scroll
      _ <- putLineSlowly("What do you want to do?", "prompt")
      _ <- prompt(">>  ")
      input <- getLine
      _ <- clearInput
      _ <- input match {
        case "quit" => putLineSlowly("OK, see you again soon!", "response")
        case "xmas" => playSound("MerryXmas.mp3")
        case "success" => playSound("success.wav")
        case "stop" => stopSound
        case x => putLineSlowly(s"OK, let's $x!", "response")
      }
    } yield ()).flatMap(_ => gameLoop)
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    (for {
      _ <- putLine(Ascii.logo, "pre")
      _ <- gameLoop()
    }yield ()).unsafeRunAsyncAndForget()
  }
}