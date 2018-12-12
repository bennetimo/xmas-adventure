package io.coderunner.adventure

import cats.data.StateT
import cats.effect.IO

import scala.scalajs.js.annotation.JSExport
import Console._
import Music._
import World._
import monocle.PLens

object Game {

  type Game[A] = StateT[IO, GameState, A]

  def validMove(currentRoom: Room, targetRoom: Room, connections: Map[Room, List[Room]]): Boolean = {
    connections.get(currentRoom).exists(_.contains(targetRoom))
  }

  import Action._
  val performAction: Action => Game[_] = {

    def tryMove(targetRoom: Room): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      _ <- if(validMove(currentRoom, targetRoom, map.connections)) {
        update(playerRoomL)(_ => targetRoom)
      } else putLineSlowly(s"You can't go to the ${targetRoom.name} from here", "response")
    } yield ()

    {
      case Look => displayRoomInfo
      case Xmas => playSound("MerryXmas.mp3")
      case Twinkle => playSound("success.wav")
      case Goto(room) => tryMove(room)
      case Stop => stopSound
      case _ => putLineSlowly("I'm sorry, I don't understand that right now", "response")
    }
  }

  def gameLoop: Game[Unit] = {
    (for {
      gs <- StateT.get[IO, GameState]
      _ <- displayRoomInfo
      _ <- scroll
      _ <- putLineSlowly(s"What do you want to do now ${gs.player.name}?", "prompt")
      _ <- scroll
      _ <- prompt(">>  ")
      action <- getLine.map(Action.parse)
      _ <- clearInput
      _ <- performAction(action)
    } yield ()).flatMap(_ => gameLoop)
  }

  def preLoop: Game[Unit] = for {
    _ <- putLine(Ascii.logo, "pre")
    _ <- putLineSlowly(Messages.intro, "response", "p", newLine = true)
    _ <- getName
    _ <- gameLoop
  } yield ()

  def updateName(name: String): Game[Unit] = state[Unit] { s: GameState => (playerNameL.modify(_ => name)(s), ()) }

  def displayRoomInfo: Game[Unit] = for {
    room <- get(playerRoomL)
    _ <- putLineSlowly(s"You are in the ${room.name}", "response", "p", false)
  } yield Unit

  def state[A](f: GameState => (GameState, A)): Game[A] = StateT[IO, GameState, A](s => IO(f(s)))

  def get[A](lens: PLens[GameState, GameState, A, A]): Game[A] = StateT[IO, GameState, A](s => IO.pure((s, lens.get(s))))

  def update[A](lens: PLens[GameState, GameState, A, A])(f: A => A): Game[A] = StateT[IO, GameState, A](s => IO.pure({
    val a2 = f(lens.get(s))
    val s2 = lens.set(a2).apply(s)
    (s2, a2)
  }))

  def nothing: Game[Unit] = state[Unit](s => (s, Unit))

  def getName: Game[Unit] = {
    for {
    _  <- putLineSlowly("What is your name?", "prompt", "p", newLine = false)
    name <- getLine
    _ <- updateName(name)
    _ <- putLineSlowly(s"Nice to meet you, $name", "response", "p", newLine = false)
    _ <- clearInput
    } yield ()
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    preLoop.run(GameState(DevonWorld.gameMap, PlayerState("", DevonWorld.diningRoom))).unsafeRunAsyncAndForget()
  }
}
