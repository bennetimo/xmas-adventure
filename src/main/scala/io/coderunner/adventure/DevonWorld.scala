package io.coderunner.adventure

import io.coderunner.adventure.World.{GameMap, Item, Room}

object DevonWorld {

  // Rooms
  val barn = Room("barn", ascii = Ascii.barn, items = List(
    Item("tractor", pickable = false),
    Item("tools", pickable = false, preposition = ""),
    Item("tablet", pickable = false)))
  val outside = Room("outside", preposition = "", ascii = Ascii.outside, items = List(
    Item("post box", pickable = false),
    Item("cars", pickable = false, preposition = ""),
    Item("vegetables", pickable = false, preposition = "some")))
  val diningRoom = Room("dining room", ascii = Ascii.diningRoom, items = List(
    Item("table", pickable = false),
    Item("coat", pickable = false),
    Item("laptop", pickable = false),
    Item("glassware cabinet", pickable = false),
    Item("goodies cabinet", pickable = false)))
  val kitchen = Room("kitchen", ascii = Ascii.kitchen, items = List(
    Item("kettle", pickable = false),
    Item("fridge", pickable = false),
    Item("recycling basket", pickable = false),
    Item("phil", pickable = false, preposition = "")
  ))
  val hallway = Room("hallway", ascii = Ascii.hallway, items = List(
    Item("freezer", pickable = false),
    Item("tumble dryer", pickable = false),
    Item("drinks cupboard", pickable = false)
  ))
  val study = Room("study", ascii = Ascii.study, items = List(
    Item("bookcase", pickable = false)
  ))
  val toilet = Room("toilet", ascii = Ascii.toilet, items = List(
    Item("sink", pickable = false),
    Item("shower", pickable = false),
    Item("toilet", pickable = false)
  ))
  val livingRoom = Room("living room", ascii = Ascii.livingRoom, items = List(
    Item("sofa", pickable = false),
    Item("phone", pickable = false),
    Item("tv", pickable = false),
    Item("stairs", pickable = false)
  ))

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