package io.coderunner.adventure

import cats.data.StateT
import cats.effect.IO
import io.coderunner.adventure.Game.Game
import org.querki.jquery.{$, JQueryEventObject}
import org.scalajs.dom.ext.KeyCode

import scala.scalajs.js

sealed trait PageElement { val id: String }
case object Prompt extends PageElement { val id = "#prompt"}
case object Input extends PageElement { val id = "#userInput"}
case object Output extends PageElement { val id = "#output"}

object Console {

  lazy val input = $(Input.id)
  lazy val output = $(Output.id)
  lazy val prompt = $(Prompt.id)

  // This has the effect of having the 'update' function of the Channel passed as the event handler, so we can
  // intercept the key press values being passed back. Each time we press a key, the handler will be called, which
  // checks the condition to see whether it was an Enter key. If so, the future is completed. This is used to
  // effectively allow us to simulate a blocking readLine input in the browser, reading from the text input. Only
  // when the player has fully entered a line does the game loop continue
  val enterPressed = new Channel[JQueryEventObject](input.keyup(_), e => e.which == KeyCode.Enter)

  val getLine: Game[String] = StateT.liftF(IO.fromFuture(IO(enterPressed())).flatMap(_ => readInput))

  val clearInput: Game[Unit] = StateT.liftF(IO(input.value("")))

  val readInput: IO[String] = IO(input.valueString)

  def prompt(s: String): Game[Unit] = StateT.liftF(IO(prompt.text(s"$s")))

  def putLine(s: String, tagType: String = "p"): Game[Unit] = StateT.liftF(IO(output.append(s"<$tagType>$s</$tagType>")))

  def putLineSlowly(s: String, cssClass: String, tagType: String = "p", newLine: Boolean = false): Game[Boolean] = {
    val words = s.split(" ").zipWithIndex

    def slowly(f: Boolean => Unit): Unit = {
      output.append(s"""<$tagType class="$cssClass"></$tagType>""")

      words.foreach { case (s, i) =>
        js.timers.setTimeout(80 * i) {
          $(s"${Output.id} p.$cssClass:last").append(s"$s ")
          if(i >= words.length - 1){
            if(newLine)
              output.append(s"""<br />""")
            f(true)
          }
        }
      }
    }

    val textFinished = new Channel[Boolean](slowly, _ == true)

    StateT.liftF(IO.fromFuture(IO(textFinished())))
  }

  def scroll: Game[Unit] = StateT.liftF(IO {
    val screen = org.scalajs.dom.document.getElementById(Output.id.tail)
    screen.scrollTop = screen.scrollHeight.toDouble
  })

}
