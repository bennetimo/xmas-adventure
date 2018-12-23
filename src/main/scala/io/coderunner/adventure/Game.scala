package io.coderunner.adventure

import cats.data.StateT
import cats.effect.IO

import scala.scalajs.js.annotation.JSExport
import Console._
import Music._
import World._
import Util._
import monocle.PLens

object Game {

  type Game[A] = StateT[IO, GameState, A]

  def validMove(currentRoom: Room, targetRoom: Room, connections: Map[Room, List[Room]]): Boolean = {
    connections.get(currentRoom).exists(a => a.map(_.name.toLowerCase()).contains(targetRoom.name.toLowerCase))
  }

  def findTargetRoom(gameMap: GameMap, targetRoom: String): Option[Room] = {
    gameMap.connections.keys.find(_.name.toLowerCase.trim == targetRoom.toLowerCase.trim)
  }

  def findTargetItem(room: Room, targetItem: String): Option[Item] = {
    room.items.find(_.name.toLowerCase.trim == targetItem.toLowerCase.trim)
  }

  import Action._
  val performAction: Action => Game[_] = {

    def tryMove(targetRoom: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      target = findTargetRoom(map, targetRoom)

      _ <- target.map( room => {
        if(validMove(currentRoom, room, map.connections))
                  update(playerRoomL)(_ => room).flatMap(_ => clearOutput).flatMap(_ => displayRoomInfo)
                else putLineSlowly(s"You can't get to $targetRoom from here", "response")
      }).getOrElse(putLineSlowly("That place does not exist!", "response"))
    } yield ()

    def tryPickUp(targetItem: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      target = findTargetItem(currentRoom, targetItem)

      _ <- target.map( item => {
        if(item.pickable)
          playSound("success.wav")
        else putLineSlowly(s"You can't pick up the ${item.name}")
      }).getOrElse(putLineSlowly("Can't see that around here..."))
    } yield ()

    def tryInspect(targetItem: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      target = findTargetItem(currentRoom, targetItem)

      _ <- target.map( item => {
        putLineSlowly(item.description)
      }).getOrElse(putLineSlowly("Can't see that around here..."))
    } yield ()

    {
      case Look => clearOutput.flatMap(_ => displayRoomInfo)
      case Xmas => playSound("MerryXmas.mp3")
      case Twinkle => playSound("success.wav")
      case Goto(room) => tryMove(room)
      case PickUp(item) => tryPickUp(item)
      case Inspect(item) => tryInspect(item)
      case Stop => stopSound
      case _ => putLineSlowly("I'm sorry, I don't understand that right now")
    }
  }

  def gameLoop: Game[Unit] = {
    (for {
      gs <- StateT.get[IO, GameState]
      _ <- scroll
      _ <- prompt(">>  ")
      action <- getLine.map(Action.parse)
      _ <- clearInput
      _ <- performAction(action)
    } yield ()).flatMap(_ => gameLoop)
  }

  def handleInputResponse(input: String): Game[Unit] = if(input.startsWith("n")) putLineSlowly("Well it is the only option, sorry!") else nothing

  def preLoop: Game[Unit] = for {
    _ <- putLine(Ascii.logo, "pre")
    _ <- putLineSlowly(Messages.introOne)
    name <- getName
//    _ <- putLineSlowly("Finding an elf...")
//    _ <- loadingBar
//    _ <- putLineSlowly(Messages.introTwo, newLine = false)
//    _ <- loadingBar
//    _ <- putLineSlowly(Messages.introThree)
//    _ <- putLineSlowly("Did you get that?")
//    input <- getLine
//    _ <- handleInputResponse(input)
//    _ <- clearInput
//    _ <- putLineSlowly(Messages.introFour)
//    input <- getLine
//    _ <- handleInputResponse(input)
//    _ <- clearInput
//    _ <- putLineSlowly("Initialising magic world...")
//    _ <- loadingBar
//    _ <- putLineSlowly("Bridging physical world...")
//    _ <- loadingBar
    _ <- putLineSlowly("Good luck! We hope that you have been nice :)")
    _ <- pause()
    _ <- clearInput
    _ <- clearOutput
    _ <- displayRoomInfo
    _ <- gameLoop
  } yield ()

  def updateName(name: String): Game[Unit] = state[Unit] { s: GameState => (playerNameL.modify(_ => name)(s), ()) }

  def displayRoomInfo: Game[Unit] = for {
    map <- get(mapL)
    room <- get(playerRoomL)
    _ <- putLine(room.ascii, "pre")
    _ <- putLineSlowly(s"You are ${room.inString}")
    _ <- putLineSlowly(room.describeItems)
    connected = map.connections.get(room).getOrElse(Nil)
    _ <- putLineSlowly(s"You can go to ${combinedString(connected)} from here")
  } yield Unit

  def state[A](f: GameState => (GameState, A)): Game[A] = StateT[IO, GameState, A](s => IO(f(s)))

  def get[A](lens: PLens[GameState, GameState, A, A]): Game[A] = StateT[IO, GameState, A](s => IO.pure((s, lens.get(s))))

  def update[A](lens: PLens[GameState, GameState, A, A])(f: A => A): Game[A] = StateT[IO, GameState, A](s => IO.pure({
    val a2 = f(lens.get(s))
    val s2 = lens.set(a2).apply(s)
    (s2, a2)
  }))

  def nothing: Game[Unit] = state[Unit](s => (s, Unit))

  def getName: Game[String] = {
    for {
    _  <- putLineSlowly("What is your name?", newLine = false)
    name <- getLine
    _ <- updateName(name)
    _ <- putLineSlowly(s"Nice to meet you, $name")
    _ <- clearInput
    } yield name
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    preLoop.run(GameState(DevonWorld.gameMap, PlayerState("", DevonWorld.diningRoom, Nil))).unsafeRunAsyncAndForget()
  }
}
