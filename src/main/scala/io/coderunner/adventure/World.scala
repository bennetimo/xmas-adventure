package io.coderunner.adventure

import io.coderunner.adventure.World.{GameMap, Room}
import monocle.Lens
import monocle.macros.GenLens

object World {

  case class GameState(gameMap: GameMap[Room], player: PlayerState)
  case class PlayerState(name: String, currentRoom: Room)

  case class GameMap[A](rooms: List[A], connections: Map[Room, List[Room]])
  case class Room(name: String)

  val mapL: Lens[GameState, GameMap[Room]] = GenLens[GameState](_.gameMap)
  val playerL: Lens[GameState, PlayerState] = GenLens[GameState](_.player)
  val nameL = GenLens[PlayerState](_.name)
  val roomL = GenLens[PlayerState](_.currentRoom)
  val playerNameL = playerL composeLens nameL
  val playerRoomL = playerL composeLens roomL

}

object DevonWorld {

  val diningRoom = Room("Dining Room")
  val kitchen = Room("Kitchen")
  val hallway = Room("Hallway")
  val drinksCupboard = Room("Drinks Cupboard")

  val rooms = List(diningRoom, kitchen, hallway, drinksCupboard)
  val connections = Map(
    diningRoom -> List(kitchen),
    kitchen -> List(diningRoom, hallway),
    hallway -> List(kitchen, drinksCupboard),
    drinksCupboard -> List(hallway)
  )

  val gameMap = GameMap(rooms, connections)

}