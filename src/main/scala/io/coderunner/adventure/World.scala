package io.coderunner.adventure

import io.coderunner.adventure.World.{GameMap, Item, Room}
import monocle.Lens
import monocle.macros.GenLens

object World {

  case class GameState(gameMap: GameMap, player: PlayerState)
  case class PlayerState(name: String, currentRoom: Room, items: List[Item])

  case class GameMap(connections: Map[Room, List[Room]])
  case class Room(name: String, items: List[Item], ascii: String = "") {
    def describeItems: String = if(items.isEmpty) "There's nothing of interest here" else "You can see " + items.map(_.name).mkString(", ")
  }
  case class Item(name: String, pickable: Boolean)

  val mapL: Lens[GameState, GameMap] = GenLens[GameState](_.gameMap)
  val playerL: Lens[GameState, PlayerState] = GenLens[GameState](_.player)
  val nameL = GenLens[PlayerState](_.name)
  val roomL = GenLens[PlayerState](_.currentRoom)
  val playerNameL = playerL composeLens nameL
  val playerRoomL = playerL composeLens roomL

}

object DevonWorld {

  // Items
  //
  val tractor = Item("Tractor", true)

  val table = Item("Table", false)
  val coat = Item("Coat", false)
  val postBox = Item("Post Box", false)

  val drinksCupboard = Item("Drinks Cupboard", false)

  // Rooms
  val barn = Room("Barn", List(tractor), ascii = Ascii.barn)
  val outside = Room("Outside", List(postBox), ascii = Ascii.outside)
  val diningRoom = Room("Dining Room", List(table, coat), ascii = Ascii.diningRoom)
  val kitchen = Room("Kitchen", Nil, ascii = Ascii.kitchen)
  val hallway = Room("Hallway", Nil, ascii = Ascii.hallway)
  val study = Room("Study", Nil, ascii = Ascii.study)
  val toilet = Room("Toilet", Nil, ascii = Ascii.toilet)
  val livingRoom = Room("Living Room", Nil, ascii = Ascii.livingRoom)


  val connections = Map(
    barn -> List(outside),
    outside -> List(barn, diningRoom),
    diningRoom -> List(kitchen, outside),
    kitchen -> List(diningRoom, hallway),
    hallway -> List(kitchen, toilet, livingRoom, study),
    study -> List(hallway),
    toilet -> List(hallway),
    livingRoom -> List(hallway),
  )

  val gameMap = GameMap(connections)

}