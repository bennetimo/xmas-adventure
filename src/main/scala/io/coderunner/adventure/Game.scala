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

  def lookupItem(items: List[Item], targetItem: String): Option[Item] = items.find(_.name.toLowerCase.trim == targetItem.toLowerCase.trim)

  def findTargetItem(room: Room, atItem: Option[Item], targetItem: String, secretItems: Map[Item, List[Item]]): Option[Item] = {
    lazy val visibleItem = lookupItem(room.items, targetItem)
    lazy val hiddenItem = atItem.map(_.hiddenItems).flatMap(items => lookupItem(items, targetItem))
    lazy val secretItem = lookupItem(atItem.flatMap(item => secretItems.get(item)).getOrElse(Nil), targetItem)

    visibleItem orElse hiddenItem orElse secretItem
  }

  import Action._
  val performAction: Action => Game[_] = {

    def tryMove(targetRoom: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      target = findTargetRoom(map, targetRoom)

      _ <- target.map( room => {
        if(validMove(currentRoom, room, map.connections))
                  for {
                    _ <- stopSound // Cancel any current sounds
                    _ <- room.sound.map(s => playSound(s)).getOrElse(nothing)
                    _ <- update(playerRoomL)(_ => room).flatMap(_ => clearOutput).flatMap(_ => displayRoomInfo)
                    _ <- update(playerAtItemL)(_ => None) //No longer at an item if moving
                  } yield ()
                else putLineSlowly(s"You can't get to $targetRoom from here")
      }).getOrElse(putLineSlowly("That place does not exist!"))
    } yield ()

    def tryPickUp(targetItem: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      atItem <- get(playerAtItemL)
      secretItems <- get(secretItemsL)
      target = findTargetItem(currentRoom, atItem, targetItem, secretItems)

      _ <- target.map( item => {
        if(item.pickable)
          for {
            _ <- playSound("success.wav")
            _ <- putLineSlowly(s"You have picked up $item")
            _ <- update(playerInventoryL)(i => item :: i)
            _ <- if(item.realWorld) putLineSlowly(s"Our elves have placed this item into your physical world, you need to find it in the corresponding place!").flatMap(_ => playSound("tinkle.wav")) else nothing
//            _ <- update(playerRoomL)(_ => room)
          } yield ()
        else putLineSlowly(s"You can't pick up the ${item.name}")
      }).getOrElse(putLineSlowly("Can't see that around here..."))
    } yield ()

    def tryInspect(targetItem: String): Game[Unit] = for {
      map <- get(mapL)
      currentRoom <- get(playerRoomL)
      atItem <- get(playerAtItemL)
      secretItems <- get(secretItemsL)
      target = findTargetItem(currentRoom, atItem, targetItem, secretItems)

      _ <- target.map( item => {
        for {
          _ <- update(playerAtItemL)(_ => target)
          _ <- item.sound.map(s => stopSound.flatMap(_ => playSound(s))).getOrElse(nothing)
          _ <- putLineSlowly(item.description)
          _ <- if(item.hiddenItems.isEmpty) nothing else putLineSlowly(item.hidden(secretItems.get(item).getOrElse(Nil)))
        } yield ()
      }).getOrElse(putLineSlowly("Can't see that around here..."))
    } yield ()

    def tryUse(targetItem: String): Game[Unit] = for {
      map <- get(mapL)
      inventory <- get(playerInventoryL)
      target = lookupItem(inventory, targetItem)

      _ <- target.map( item => item.performAction).getOrElse(putLineSlowly("You don't have that"))
    } yield ()

    {
      case Look => clearOutput.flatMap(_ => displayRoomInfo)
      case Win => win
      case Twinkle => playSound("success.wav")
      case Goto(room) => tryMove(room)
      case PickUp(item) => tryPickUp(item)
      case Inspect(item) => tryInspect(item)
      case Use(item) => tryUse(item)
      case Inventory => displayInventory
      case Stop => stopSound
      case FindFirstKey => for {
        _ <- realItemFound
        _ <- putLineSlowly("Well done, you found a key! Now... what do you suppose it is for, hmm?")
      } yield ()
      case FindMoneyKey => realItemFound.flatMap(_ => putLineSlowly("A key! Just need a lock to open now..."))
      case FindPlayingCards => realItemFound.flatMap(_ => putLineSlowly("I don't really fancy a game right now... maybe the elves left this here to send me a clue? It's just a standard pack of cards...isn't it?"))
      case FindBook => realItemFound.flatMap(_ => putLineSlowly("Well now, how did that get here? Must be important if it's hidden away like this"))
      case FindSecondBox => realItemFound.flatMap(_ => putLineSlowly("You're well on your way to proving yourself this year. You seem to have figured out what to do by now... let's see if you can find out how to open this next box by finding three hidden numbers"))
      case FindThirdBox => realItemFound.flatMap(_ => putLineSlowly("Just when you were thinking the elves had finally finished challenging you. No lock you thought! But what do we have here?? A mysterious small box and... oh! You have unlocked your hat, where it with pride! Don't forget to add the arms at his hat (you did find that, didn't you?)"))
      case FindMoneyBox => realItemFound.flatMap(_ => putLineSlowly("Ca Ching...! I'm rich! Oh... well I won't go hungry. Hmm hold on, there's something else in here too. Wonder what the coloured squares mean? And that title is... odd"))
      case FindPorkScratchings => realItemFound.flatMap(_ => putLineSlowly("A tasty treat! Be sure to share!"))
      case FindGoldBear => realItemFound.flatMap(_ => putLineSlowly("Nice, you found the golden bear! Our workshop decided you needed a treat to keep going!"))
      case FindSantaHat => realItemFound.flatMap(_ => putLineSlowly("It's a hat! Don't worry, it's not for your head. Tim is not that lazy..."))

      case _ => putLineSlowly("I'm sorry, I don't understand that right now")
    }
  }

  def realItemFound: Game[Unit] = {
    for {
      name <- get(playerNameL)
      _ <- playSound("collect.wav")
      _ <- update(playerRealItemsFoundL)(count => count + 1)
      count <- get(playerRealItemsFoundL)
      _ <- putLineSlowly(s"Nicely done $name, you've found $count/10 items we've placed into your physical word!")
      _ <- if(count >= 10) putLineSlowly("ALL items found! Well done!") else nothing
    } yield ()
  }

  def win: Game[Unit] = {
    for {
      _ <- clearOutput
      _ <- putLineSlowly(Messages.win)
      _ <- pause()
      _ <- playSound("MerryXmas.mp3")
      _ <- putLine(Ascii.win, "pre")
    } yield ()
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
    _ <- putLineSlowly("Finding an elf...")
    _ <- loadingBar
    _ <- putLineSlowly(Messages.introTwo, newLine = false)
    _ <- loadingBar
    _ <- putLineSlowly(Messages.introThree)
    _ <- putLineSlowly("Did you get that?")
    input <- getLine
    _ <- handleInputResponse(input)
    _ <- clearInput
    _ <- putLineSlowly(Messages.introFour)
    input <- getLine
    _ <- handleInputResponse(input)
    _ <- clearInput
    _ <- putLineSlowly("Initialising magic world...")
    _ <- loadingBar
    _ <- putLineSlowly("Bridging physical world...")
    _ <- loadingBar
    _ <- putLineSlowly("Good luck! We hope that you have been nice :)")
    _ <- pause()
    _ <- clearInput
    _ <- clearOutput
    _ <- displayRoomInfo
    _ <- gameLoop
  } yield ()

  def updateName(name: String): Game[Unit] = state[Unit] { s: GameState => (playerNameL.modify(_ => name)(s), ()) }

  def displayInventory: Game[Unit] = for {
    map <- get(mapL)
    inventory <- get(playerInventoryL)
    _ <- if(inventory.isEmpty) putLineSlowly("You don't have anything") else putLineSlowly(s"You have ${combinedString(inventory)}")
  } yield Unit

  def displayRoomInfo: Game[Unit] = for {
    map <- get(mapL)
    room <- get(playerRoomL)
    _ <- putLine(room.ascii, "pre")
    _ <- putLineSlowly(s"You are ${room.inString}")
    _ <- room.description.map(s => putLineSlowly(s)).getOrElse(nothing)
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
    preLoop.run(GameState(DevonWorld.gameMap, PlayerState("", DevonWorld.diningRoom, Nil, None, 0))).unsafeRunAsyncAndForget()
  }
}
