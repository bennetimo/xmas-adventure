package io.coderunner.adventure

import io.coderunner.adventure.Util.combinedString
import monocle.Lens
import monocle.macros.GenLens

object World {

  case class GameState(gameMap: GameMap, player: PlayerState)
  case class PlayerState(name: String, currentRoom: Room, items: List[Item])

  case class GameMap(connections: Map[Room, List[Room]])
  case class Room(name: String, items: List[Item], preposition: String = "the ", ascii: String = "") {
    def describeItems: String = if(items.isEmpty) "There's nothing of interest here" else "You can see " + combinedString(items)

    override def toString: String = s"$preposition ${name.toLowerCase.trim}"
    def inString: String = s"${if (preposition.isEmpty) "" else "in"} $preposition ${name.toLowerCase.trim}"
  }
  case class Item(name: String, description: String, pickable: Boolean, preposition: String = "a"){
    override def toString: String = s"$preposition ${name.toLowerCase.trim}"
  }

  val mapL: Lens[GameState, GameMap] = GenLens[GameState](_.gameMap)
  val playerL: Lens[GameState, PlayerState] = GenLens[GameState](_.player)
  val nameL = GenLens[PlayerState](_.name)
  val roomL = GenLens[PlayerState](_.currentRoom)
  val playerNameL = playerL composeLens nameL
  val playerRoomL = playerL composeLens roomL

}