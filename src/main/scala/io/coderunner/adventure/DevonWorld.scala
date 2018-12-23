package io.coderunner.adventure

import io.coderunner.adventure.World.{GameMap, Item, Room}

object DevonWorld {
  import Console._
  import ItemActions._

  // TODO
  val items = List(
    Item("pork scratchings", ""),
    Item("santa hat", ""),
  )

  // Rooms
  val barn = Room("barn", ascii = Ascii.barn, items = List(
    Item("tractor", "A legend of a machine, it only just fits! Oh, and there's something behind it", hiddenItems = List(
      Item("box", "Some kind of box", pickable = true, realWorld = true)
    )),
    Item("tools", "Lots of jobs, and right tool for the job, means lots of tools. No DIY tonight though, beer instead!", preposition = ""),
    Item("tablet", "Someone's been watching random videos on YouTube again! Not much use to me though")))
  val outside = Room("outside", preposition = "", ascii = Ascii.outside, items = List(
    Item("post box", "It's just by the road, getting a lot of use around Christmas time. Hold on, it looks like there is still something in it...", hiddenItems = List(
      Item("envelope", "It's sealed, I wonder what's inside", pickable = true, realWorld = true, preposition = "an")
    )),
    Item("cars", "Yep, just lots of cars. Bit jammed in, luckily we're not going anywhere right now", preposition = ""),
    Item("vegetables", "Maybe come back in Spring, it's all a bit barren out here now", preposition = "some")))
  val diningRoom = Room("dining room", ascii = Ascii.diningRoom, items = List(
    Item("table", "A fine table, made of solid wood. Many fun evenings have been had round here. This one too!", hiddenItems = List(
      Item("instructions", "Something useful", pickable = true, preposition = "some",
        action = useInstructions)
    )),
    Item("coats", "A very full set of coat hooks, coats everywhere!", preposition = "lots of", hiddenItems = List(
      Item("pockets", "Hmm, looks like there's something in one of these...", preposition = "some stuffed", hiddenItems = List(
        Item("key", "A small gold key! What could that be for?", pickable = true, realWorld = true, action = useLevel1Key)
      ))
    )),
    Item("laptop", "A fairly old machine, but still does the job", pickable = true,
      action = useLaptop),
    Item("glassware cabinet", "There's lots of shiny glasses in here. Can't see anything else though"),
    Item("goodies cabinet", "Hmm, there's normally somemething in here...", hiddenItems = List(
      Item("chocolate", "soft and delicious!", pickable = true, action = putLineSlowly("You enjoy a tasty piece of chocolate. Now... back to the challenge..."))
    )),
    Item("stairs", "I'll go upstairs to bed later, but right now I've got to solve this puzzle", preposition = "some")
  ))
  val teaBags = Item("tea bags", "Just your standard brew", pickable = true, preposition = "some")
  val milk = Item("milk", "Nice and cool!", pickable = true, preposition = "some")
  val sugar = Item("sugar", "For those who have it", pickable = true, preposition = "some")
  val playingCards = Item("pack of cards", "Hmm, there's something not right about this deck...", pickable = true, realWorld = true)
  val fridge = Item("fridge", "If you need scissors, they're here", hiddenItems = List(
    milk
  ))
  val kitchen = Room("kitchen", ascii = Ascii.kitchen, items = List(
    Item("kettle", "I wouldn't mind a cup of tea", pickable = true, action = useKettle, requires = List(
      teaBags, milk, sugar
    )),
    Item("shelf", "Lots of things on here", hiddenItems = List(
      teaBags, sugar
    )),
    fridge,
    Item("recycling basket", "There's just some junk mail in here. One of them is advertising a dial-a-joke service on number 888. Guess if I had a phone I could try that"),
    Item("phil", "He's doing the washing up, don't disturb his flow!", preposition = "")
  ))
  val hallway = Room("hallway", ascii = Ascii.hallway, items = List(
    Item("aiko", "Meow Meow!", preposition = ""),
    Item("freezer", "A big chest freezer, full of Christmas yummies! Hey, there's something on top of it!", preposition = "the", hiddenItems = List(
      Item("jackson", "Purr Purr...!", preposition = ""),
    )),
    Item("drinks cupboard", "It's dark in here", preposition = "the", hiddenItems = List(
      Item("light", "You turn on the light, but all you find is drinks.", preposition = "the"),
    )),
  ))
  val study = Room("study", ascii = Ascii.study, items = List(
    Item("bookcase", "There's some books on here that I love", hiddenItems = List(
      Item("strange book", "hold on a minute, there's a book here that is surely not mine! Who put it here!?... and why", pickable = true, realWorld = true)
    )),
    Item("sofa bed", "No time to sleep!"),
    Item("desk", "a useful writing surface", hiddenItems = List(
      Item("pens", "assorted types", preposition = "some"),
      Item("paper", "always useful", preposition = "some")
    )),
  ))
  val toilet = Room("toilet", ascii = Ascii.toilet, items = List(
    Item("sink", "You wash your hands. Squeaky clean!"),
    Item("shower", "It's a shower. What more can you say"),
    Item("toilet", "You don't need the toilet right now")
  ))
  val livingRoom = Room("living room", ascii = Ascii.livingRoom, items = List(
    Item("sofa", "Looks comfy, near the fire and well placed for watching TV. Makes me want to sit down!", hiddenItems = List(
      Item("key", "There's a small key that's fallen down the side between the cushions, I wonder what it is for?", pickable = true, realWorld = true,
        action = putLineSlowly("It clearly opens something... but what?"))
    )),
    Item("phone", s"There's been a few missed calls, and you got a text message from Tim: ${ItemActions.limerick}", pickable = true, action = usePhone),
    Item("tv", "Loads of Christmas films on right now! Who's got the radio times?"),
    Item("stairs", "Sorry, Santa's elves didn't have enough time to create the magic of upstairs", preposition =  "the")
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

  val unlockedItems: Map[Item, List[Item]] = Map.empty

  val gameMap = GameMap(connections, unlockedItems)

}