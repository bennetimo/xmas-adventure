package io.coderunner.adventure

import io.coderunner.adventure.Console.{clearInput, getLine, putLineSlowly}
import io.coderunner.adventure.Game.{Game}
import io.coderunner.adventure.World.playerInventoryL

import scala.util.Random
import World._
import DevonWorld._
import Game._

object ItemActions {

  def useLaptop: Game[Unit] = {
    for {
      _  <- putLineSlowly("Looks like someone was looking at something on here, but the screen is locked with a password ")
      _  <- putLineSlowly("Enter password: ")
      password <- getLine
      _ <- if(password == "festive") {
        for {
          _ <- putLineSlowly("The password was correct, and the screen is now displaying a word document")
          _ <- putLineSlowly("All that is written is: ")
          _ <- putLineSlowly("50.801033, -3.985236")
          _ <- putLineSlowly("What could it mean?")
        } yield ()
      } else for {
        _ <- putLineSlowly("Hmm, the password was incorrect")
        _ <- putLineSlowly("You better try to remember the password and then come back")
      } yield ()
      _ <- clearInput
    } yield ()
  }

  val jokes = Array("If you have three Wise Men, one Mary, and one Jesus, and you take away one " +
    "Jesus that dies on the cross. What do you have? ...Christ-Math!",
    "After Snowball three and their dog passed away, Bart and Lisa decided to get a bird instead. What did they name it? ...Santa Claws",
    "Torvil and Dean were excited to open their presents this year. They untied the ribbon, ripped off the wrapping paper, " +
      "and stared into the box. The happiness in their faces drained away and they just stared at each other incredulously. " +
      "“Yup, there’s nothing worse than some cheap skates”."
  )

  val limerick =
    """
      |<br/>There once was a family that moved to Devon
      |<br/>To have their own little slice of Heaven
      |<br/>They had merry times
      |<br/>Drank way too much wine
      |<br/>Until they collapsed asleep, all seven
      |<br/>
      |<br/>Now if you want to hear some more rhyme
      |<br/>Before you’re fed up and call time
      |<br/>Then check out what’s next
      |<br/>A five line piece of text
      |<br/>Hopefully you find it sublime
      |<br/>
      |<br/>You might be thinking this is a strange cracker
      |<br/>For that man Ian who bought a tractor
      |<br/>But I’m sure he appreciates something quirky
      |<br/>Not just brussels and turkey
      |<br/>And that’s the determining factor
    """.stripMargin

  def usePhone: Game[Unit] = {
    for {
      _  <- putLineSlowly("What number do you want to call?")
      number <- getLine
      _ <- if(number == "888") {
        for {
          _ <- putLineSlowly("It's ringing...")
          _ <- putLineSlowly("Brp Brp... Brp Brp...")
          _ <- putLineSlowly("Hello! Welcome to the CRACK A JOKE service! Dial a digit now for a joke")
          rnd = new Random(System.currentTimeMillis()).nextInt(jokes.length - 1)
          _ <- putLineSlowly(jokes(rnd))
          _ <- putLineSlowly("Call back again and you might get a different joke!")
        } yield ()
      } else for {
        _ <- putLineSlowly("Hmm, that number didn't work")
      } yield ()
      _ <- clearInput
    } yield ()
  }

  def useKettle: Game[Unit] = {
    for {
      _  <- putLineSlowly("You stick the kettle on to boil and prepare all the cups. Yep that's right, everyone wanted a cup AGAIN. Of course.")
      _  <- putLineSlowly("While you're waiting for it to brew, you turn around and notice something on top of the fridge...")
      unlocked <- get(secretItemsL)
      current = unlocked.get(fridge)
      _ <- update(secretItemsL)(m => m.updated(fridge, playingCards :: current.getOrElse(Nil)))
    } yield ()
  }

  def useInstructions: Game[Unit] = putLineSlowly(
    """
      |Santa's elves have tasked you to open your present by solving a series of puzzles.
      |Try to move around and interact with the magical environment, it should be familiar to you.
      |If you get stuck or it's too hard... well try harder! Or maybe you were just naughty after all...
    """.stripMargin)

  def useLevel1Key: Game[Unit] = putLineSlowly(
    """
      |This item has to be used in the physical world!
    """.stripMargin)

}
