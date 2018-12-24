package io.coderunner.adventure

import io.coderunner.adventure.World.Room

import scala.util.parsing.combinator._

sealed trait Action

object Action {

  private object parser extends RegexParsers {

    def word: Parser[String]   = """[a-zA-Z ]+""".r ^^ { _.toString }

    lazy val goto: Parser[Action] = ("go" | "move") ~ ("to"?) ~ ("the"?) ~ word ^^ { case _ ~ room => Goto(room) }

    lazy val pickUp: Parser[Action] = ("pick" | "take" | "grab") ~ ("up"?) ~ word ^^ { case _ ~ item => PickUp(item) }
    lazy val inspect: Parser[Action] = ("inspect" | "examine" | "look in" | "look at" | "look on" | "look inside" | "open") ~ ("the"?) ~ word ^^ { case _ ~ item => Inspect(item) }
    lazy val look: Parser[Action] = "look" ^^^ Look
    lazy val inventory: Parser[Action] = ("inventory" | "items" | "backpack") ^^^ Inventory
    lazy val use: Parser[Action] = ("use" | "view") ~ ("the"?) ~ word ^^ { case _ ~ item => Use(item) }

    // Physical items to find
    lazy val firstKey: Parser[Action] = "jinglejangle" ^^^ FindFirstKey
    lazy val moneyKey: Parser[Action] = "bowlfullofjelly" ^^^ FindMoneyKey
    lazy val playingCards: Parser[Action] = "starofwonder" ^^^ FindPlayingCards
    lazy val book: Parser[Action] = "rudolphrednose" ^^^ FindBook
    lazy val findSecondBox: Parser[Action] = "frostythesnowman" ^^^ FindSecondBox
    lazy val findThirdBox: Parser[Action] = "merryxmastoall" ^^^ FindThirdBox
    lazy val moneybox: Parser[Action] = "chocolategold" ^^^ FindMoneyBox
    lazy val porkscratchings: Parser[Action] = "omnomnom" ^^^ FindPorkScratchings
    lazy val goldbear: Parser[Action] = "goldenyummy" ^^^ FindGoldBear

    lazy val win: Parser[Action] = "christmascrackered" ^^^ Win

    lazy val twinkle: Parser[Action] = "twinkle" ^^^ Twinkle
    lazy val stop: Parser[Action] = "stop" ^^^ Stop

    def grammar: Parser[Action] =
      firstKey | moneyKey | playingCards | book | findSecondBox | findThirdBox | moneybox | porkscratchings | goldbear |
        goto | pickUp | inspect | inventory | use | look | win | twinkle | stop

  }

  import parser._
  def parse(line: String): Action = parseAll(grammar, line) match {
    case Success(a, _) => a
    case failure: NoSuccess => Unknown
  }

  case object Look extends Action
  case class Goto(room: String) extends Action
  case class PickUp(item: String) extends Action
  case class Inspect(item: String) extends Action
  case class Use(item: String) extends Action

  case object FindFirstKey extends Action
  case object FindMoneyKey extends Action
  case object FindPlayingCards extends Action
  case object FindBook extends Action
  case object FindSecondBox extends Action
  case object FindThirdBox extends Action
  case object FindMoneyBox extends Action
  case object FindPorkScratchings extends Action
  case object FindGoldBear extends Action

  case object Inventory extends Action
  case object Win extends Action
  case object Twinkle extends Action
  case object Stop extends Action
  case object Unknown extends Action

}
