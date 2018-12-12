package io.coderunner.adventure

import cats.data.StateT
import cats.effect.IO
import io.coderunner.adventure.Game.Game
import org.querki.jquery.$

import scala.scalajs.js

object Music {

  val audio = $("#audio")

  def playSound(file: String): Game[Unit] = StateT.liftF(IO{
    lazy val document = js.Dynamic.global.document
    lazy val audioDom = document.getElementById("audio")

    audio.attr("src", file)
    audioDom.play()
  })

  val stopSound: Game[Unit] = StateT.liftF(IO(audio.attr("src", "")))

}
