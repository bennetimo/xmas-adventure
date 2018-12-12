package io.coderunner.adventure

import monocle.macros.GenLens

object World {

  case class PlayerState(name: String, health: Int)
  case class GameState(player: PlayerState)

  val playerL = GenLens[GameState](_.player)
  val nameL = GenLens[PlayerState](_.name)

  val playerNameL = playerL composeLens nameL

}
