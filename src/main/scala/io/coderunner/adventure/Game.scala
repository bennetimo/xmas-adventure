package io.coderunner.adventure

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._

import scala.io.StdIn.readLine

object Game extends IOApp {

  def getLine: IO[String] = IO(readLine())
  def putLine(s: String): IO[Unit] = IO(println(s))

  def gameLoop(): IO[Unit] = {
    for {
      _ <- putLine("What do you want to do?")
      input <- getLine
      _ <- input match {
        case "quit" => putLine("OK, see you again soon!")
        case x => putLine(s"OK, let's $x!").flatMap(_ => gameLoop)
      }
    } yield ()
  }

  def run(args: List[String]): IO[ExitCode] = gameLoop().as(ExitCode.Success)
}
