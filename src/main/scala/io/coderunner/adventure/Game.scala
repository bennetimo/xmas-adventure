package io.coderunner.adventure

import cats.data.StateT
import cats.effect.IO
import scala.scalajs.js.annotation.JSExport
import Console._
import Music._
import World._

object Game {

  type Game[A] = StateT[IO, GameState, A]

  def gameLoop: Game[Unit] = {
    (for {
      gs <- StateT.get[IO, GameState]
      _ <- scroll
      _ <- putLineSlowly(s"What do you want to do now ${gs.player.name}?", "prompt")
      _ <- prompt(">>  ")
      input <- getLine
      _ <- clearInput
      _ <- input match {
        case "quit" => putLineSlowly("OK, see you again soon!", "response")
        case "xmas" => playSound("MerryXmas.mp3")
        case "success" => playSound("success.wav")
        case "stop" => stopSound
        case x => putLineSlowly(s"OK, let's $x!", "response", "p", newLine = true)
      }
    } yield ()).flatMap(_ => gameLoop)
  }

  def preLoop: Game[Unit] = for {
    _ <- putLine(Ascii.logo, "pre")
    _ <- putLineSlowly(Messages.intro, "response", "p", newLine = true)
    _ <- getName
    _ <- gameLoop
  } yield ()

  def updateName(name: String): Game[Unit] = state[Unit] { s: GameState => (playerNameL.modify(_ => name)(s), ()) }

  def state[A](f: GameState => (GameState, A)): Game[A] = StateT[IO, GameState, A](s => IO(f(s)))

  def getName: Game[Unit] = {
    for {
    _  <- putLineSlowly("What is your name?", "prompt", "p", newLine = false)
    name <- getLine
    _ <- updateName(name)
    _ <- putLineSlowly(s"Nice to meet you, $name", "response", "p", newLine = false)
    } yield ()
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    preLoop.run(GameState(PlayerState("", 100))).unsafeRunAsyncAndForget()
  }
}
