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
  val barn = Room("barn", description = Some("Ah a favourite retreat. Many a good YouTube video watched in here"), ascii = Ascii.barn, items = List(
    Item("tractor", "A legend of a machine, it only just fits!"),
    Item("storage crates", "Everything you need, all found on one itemised spreadsheet. Amazing! Oh wait... and something else that is unaccounted for", preposition = "some", hiddenItems = List(
      Item("box", "Some kind of box...", pickable = true, realWorld = true)
    )),
    Item("tools", "Lots of jobs, and right tool for the job, means lots of tools. No DIY tonight though, beer instead!", preposition = ""),
    Item("tablet", "Someone's been watching random videos on YouTube again! Not much use to me though")))
  val outside = Room("outside", description = Some("Brr! It's chilly out here!"), preposition = "", ascii = Ascii.outside, items = List(
    Item("post box", "It's just by the road, getting a lot of use around Christmas time. Hold on, it looks like there is still something in it...", hiddenItems = List(
      Item("envelope", "It's sealed, I wonder what's inside", pickable = true, realWorld = true, preposition = "an")
    )),
    Item("cars", "Yep, just lots of cars. Bit jammed in, luckily we're not going anywhere right now", preposition = ""),
    Item("pip", "She's having a smoke", preposition = ""),
    Item("vegetables", "Maybe come back in Spring, it's all a bit barren out here now", preposition = "some")),
    sound = Some("birds.wav"))
  val diningRoom = Room("dining room", description = Some("It's festively decorated with shrubbery from the garden and signs that Saint Eat A Lot soon will be there"), ascii = Ascii.diningRoom, items = List(
    Item("table", "A fine table, made of solid wood. Many fun evenings have been had round here. This one too!", hiddenItems = List(
      Item("instructions", "Something useful", pickable = true, preposition = "some",
        action = useInstructions),
      Item("holly", "The daughter, not the plant", preposition = "")
    )),
    Item("coats", "A very full set of coat hooks, coats everywhere!", preposition = "lots of", hiddenItems = List(
      Item("pockets", "Hmm, looks like there's something in one of these...", preposition = "some stuffed", hiddenItems = List(
        Item("key", "A small gold key! What could that be for?", pickable = true, realWorld = true, action = useLevel1Key)
      ))
    )),
    Item("laptop", "A fairly old machine, but still does the job", pickable = true,
      action = useLaptop),
    //Item("glassware cabinet", "There's lots of shiny glasses in here. Can't see anything else though"),
    Item("goodies cabinet", "Hmm, there's normally something in here...", hiddenItems = List(
      Item("chocolates", "soft and delicious", preposition = "some", pickable = true, action = putLineSlowly("You enjoy a tasty piece of chocolate. Now... back to the challenge..."))
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
  val kitchen = Room("kitchen", description = Some("Tea, coffee, food... you name it, tasty stuff is made here"), ascii = Ascii.kitchen, items = List(
    Item("kettle", "I wouldn't mind a cup of tea", pickable = true, action = useKettle, requires = List(
      teaBags, milk, sugar
    )),
    Item("shelf", "Lots of things on here", hiddenItems = List(
      teaBags, sugar
    )),
    fridge,
    Item("recycling basket", "There's just some junk mail in here. One of them is advertising a dial-a-joke service on number 888. Guess if I had a phone I could try that"),
    Item("phil", "He's doing the washing up, don't disturb his flow!", preposition = "", sound = Some("washingup.wav"))
  ))
  val goldbear = Item("mystery gift", "You've never had one of these", pickable = true, realWorld = true)
  val drinksCupboard = Item("drinks cupboard", "It's dark in here and the light is broken", preposition = "the", hiddenItems = List(
    Item("torch", "this could be useful", pickable = true, preposition = "a", action = useLightswitch),
  ))
  val hallway = Room("hallway", description = Some("I go through here to get to other places. Or, to do my laundry"), ascii = Ascii.hallway, items = List(
    Item("aiko", "Meow Meow!", preposition = "", sound = Some("meow.wav")),
    Item("freezer", "A big chest freezer, full of Christmas yummies! Hey, there's something on top of it!", preposition = "the", hiddenItems = List(
      Item("jackson", "Purr Purr...!", preposition = "", sound = Some("purr.wav")),
    )),
    drinksCupboard,
    Item("abi", "Pouring a pint. How many is that now?!", preposition = ""),
  ))
  val study = Room("study", description = Some("Sometimes a bedroom, sometimes a study. All the time useful."), ascii = Ascii.study, items = List(
    Item("desk", "A sublimely useful piece of furniture", hiddenItems = List(
      Item("strange book", "hold on a minute, there's a book here that is surely not mine! Who put it here!?... and why", pickable = true, realWorld = true),
      Item("pens", "assorted types", preposition = "some"),
    )),
    Item("sofa bed", "No time to sleep!"),
    Item("picture", "very nice, but no time to stand around admiring")
  ))
  val toilet = Room("toilet", description = Some("A frequently used room"), ascii = Ascii.toilet, items = List(
    Item("sink", "You wash your hands. Squeaky clean!"),
    Item("shower", "It's a shower. What more can you say"),
    Item("toilet", "You don't need the toilet right now")
  ))
  val livingRoom = Room("living room", description = Some("Stockings were hung by the chimney with care, in the hope that chocolates soon would be in there"), ascii = Ascii.livingRoom, items = List(
    Item("sofa", "Looks comfy, near the fire and well placed for watching TV. Makes me want to sit down!", hiddenItems = List(
      Item("key", "There's a small key that's fallen down the side between the cushions, I wonder what it is for?", pickable = true, realWorld = true,
        action = putLineSlowly("It clearly opens something... but what?"))
    )),
    Item("phone", s"There's been a few missed calls, and you got a text message from Tim: ${ItemActions.limerick}", pickable = true, action = usePhone),
    Item("christmas tree", s"Beautifully decorated, you enjoy looking at it for a few moments"),
    Item("tv", "Not much on right now, who's got the radio times to find all the Christmas films??", sound = Some("tv.wav")),
    Item("stairs", "Sorry, Santa's elves didn't have enough time to create the magic of upstairs", preposition =  "the"),
    Item("fireplace", "Where stockings are hung with care", preposition =  "a", sound = Some("fire.wav"),  hiddenItems = List(
      Item("poker", "Useful to keep the fire going", pickable = false),
      Item("pots", "One of these has something strange inside!", preposition = "some", pickable = false, hiddenItems = List(
        Item("mystery gift", "a firm favourite", pickable = true, realWorld = true, action = putLineSlowly("YUMMY"))
      ))
    ))
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