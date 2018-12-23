package io.coderunner.adventure

import io.coderunner.adventure.World.Room

import scala.util.parsing.combinator._

sealed trait Action

object Action {

  private object parser extends RegexParsers {

    def word: Parser[String]   = """[a-zA-Z ]+""".r ^^ { _.toString }

    lazy val goto: Parser[Action] = ("go" | "move") ~ ("to"?) ~ ("the"?) ~ word ^^ { case _ ~ room => Goto(room) }

    lazy val pickUp: Parser[Action] = ("pick" | "take" | "grab") ~ ("up"?) ~ word ^^ { case _ ~ item => PickUp(item) }
    lazy val inspect: Parser[Action] = ("inspect" | "examine" | "look in" | "look at" | "look inside") ~ ("the"?) ~ word ^^ { case _ ~ item => Inspect(item) }
    lazy val look: Parser[Action] = "look" ^^^ Look
    lazy val inventory: Parser[Action] = ("inventory" | "items" | "backpack") ^^^ Inventory
    lazy val use: Parser[Action] = ("use" | "view") ~ ("the"?) ~ word ^^ { case _ ~ item => Use(item) }

    lazy val win: Parser[Action] = "xmaswinner" ^^^ Win
    lazy val twinkle: Parser[Action] = "twinkle" ^^^ Twinkle
    lazy val stop: Parser[Action] = "stop" ^^^ Stop

    def grammar: Parser[Action] =  goto | pickUp | inspect | inventory | use | look | win | twinkle | stop

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
  case object Inventory extends Action
  case object Win extends Action
  case object Twinkle extends Action
  case object Stop extends Action
  case object Unknown extends Action

}
