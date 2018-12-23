package io.coderunner.adventure

import io.coderunner.adventure.World.{GameMap, Item, Room}

object DevonWorld {
  import Console._

  // Rooms
  val barn = Room("barn", ascii = Ascii.barn, items = List(
    Item("tractor", ""),
    Item("tools", "", preposition = ""),
    Item("tablet", "")))
  val outside = Room("outside", preposition = "", ascii = Ascii.outside, items = List(
    Item("post box", ""),
    Item("cars", "", preposition = ""),
    Item("vegetables", "Maybe come back in Spring, it's all a bit barren out here now", preposition = "some")))
  val diningRoom = Room("dining room", ascii = Ascii.diningRoom, items = List(
    Item("table", "A fine table, made of solid wood", hiddenItems = List(
      Item("instructions", "Something useful", pickable = true, preposition = "some",
        action = putLineSlowly("blah blah blah"))
    )),
    Item("coat", "One of the pockets seems to have a strange object inside..."),
    Item("laptop", ""),
    Item("glassware cabinet", ""),
    Item("goodies cabinet", "")))
  val kitchen = Room("kitchen", ascii = Ascii.kitchen, items = List(
    Item("kettle", ""),
    Item("fridge", ""),
    Item("recycling basket", ""),
    Item("phil", "", preposition = "")
  ))
  val hallway = Room("hallway", ascii = Ascii.hallway, items = List(
    Item("freezer", ""),
    Item("tumble dryer", ""),
    Item("drinks cupboard", "")
  ))
  val study = Room("study", ascii = Ascii.study, items = List(
    Item("bookcase", "")
  ))
  val toilet = Room("toilet", ascii = Ascii.toilet, items = List(
    Item("sink", ""),
    Item("shower", ""),
    Item("toilet", "")
  ))
  val livingRoom = Room("living room", ascii = Ascii.livingRoom, items = List(
    Item("sofa", ""),
    Item("phone", ""),
    Item("tv", ""),
    Item("stairs", "")
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