package io.coderunner.adventure

import io.coderunner.adventure.World.Room

import scala.util.parsing.combinator._

sealed trait Action

object Action {

  private object parser extends RegexParsers {

    def room: Parser[Room]   = """[a-zA-Z ]+""".r       ^^ { s => Room.apply(s.toString) }

    lazy val look: Parser[Action] = "look" ^^^ Look
    lazy val goto: Parser[Action] = "goto " ~ room ^^ { case _ ~ room => Goto(room)}
    lazy val xmas: Parser[Action] = "xmas" ^^^ Xmas
    lazy val twinkle: Parser[Action] = "twinkle" ^^^ Twinkle
    lazy val stop: Parser[Action] = "stop" ^^^ Stop

    def grammar: Parser[Action] = look | goto | xmas | twinkle | stop

  }

  import parser._
  def parse(line: String): Action = parseAll(grammar, line) match {
    case Success(a, _) => a
    case failure: NoSuccess => Unknown
  }

  case object Look extends Action
  case class Goto(room: Room) extends Action
  case object Xmas extends Action
  case object Twinkle extends Action
  case object Stop extends Action
  case object Unknown extends Action

}