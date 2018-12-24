package io.coderunner.adventure

import io.coderunner.adventure.Console.putLineSlowly
import io.coderunner.adventure.Game.{Game, get, lookupItem}
import io.coderunner.adventure.Util.combinedString
import monocle.Lens
import monocle.macros.GenLens

object World {

  case class GameState(gameMap: GameMap, player: PlayerState)
  case class PlayerState(name: String, currentRoom: Room, inventory: List[Item], atItem: Option[Item], realItemsFound: Int)

  case class GameMap(connections: Map[Room, List[Room]], unlockedItems: Map[Item, List[Item]])
  case class Room(name: String, items: List[Item], preposition: String = "the ", ascii: String = "", sound: Option[String] = None, description: Option[String] = None) {
    def describeItems: String = if(items.isEmpty) "There's nothing of interest here" else "You can see " + combinedString(items)

    override def toString: String = s"$preposition ${name.toLowerCase.trim}"
    def inString: String = s"${if (preposition.isEmpty) "" else "in"} $preposition ${name.toLowerCase.trim}"
  }
  case class Item(name: String, description: String,
                  hiddenItems: List[Item] = Nil,
                  action: Game[Unit] = Console.putLineSlowly("You can't use that"),
                  requires: List[Item] = Nil,
                  pickable: Boolean = false,
                  realWorld: Boolean = false,
                  preposition: String = "a",
                  sound: Option[String] = None){
    override def toString: String = s"$preposition ${name.toLowerCase.trim}"
    def hidden(secret: List[Item] = Nil): String = if(hiddenItems.isEmpty) "" else s"There is ${combinedString(hiddenItems ++ secret)}"
    def performAction: Game[Unit] = for {
      inventory <- get(playerInventoryL)
      _ <- if(requires.map(_.name).toSet -- inventory.map(_.name).toSet == Set.empty) action
           else putLineSlowly(s"You can't use the $name until you have ${combinedString(requires)}")
    } yield ()
  }

  val mapL: Lens[GameState, GameMap] = GenLens[GameState](_.gameMap)
  val playerL: Lens[GameState, PlayerState] = GenLens[GameState](_.player)
  val nameL = GenLens[PlayerState](_.name)
  val unlockedItemsL = GenLens[GameMap](_.unlockedItems)
  val roomL = GenLens[PlayerState](_.currentRoom)
  val atItemL = GenLens[PlayerState](_.atItem)
  val inventoryL = GenLens[PlayerState](_.inventory)
  val realItemsFoundL = GenLens[PlayerState](_.realItemsFound)
  val playerNameL = playerL composeLens nameL
  val playerRoomL = playerL composeLens roomL
  val playerAtItemL = playerL composeLens atItemL
  val playerInventoryL = playerL composeLens inventoryL
  val secretItemsL = mapL composeLens unlockedItemsL
  val playerRealItemsFoundL = playerL composeLens realItemsFoundL

}