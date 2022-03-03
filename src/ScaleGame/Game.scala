package ScaleGame

import scala.collection.mutable.Buffer
import scala.util.Random

class Game (val playerAmount: Int, val weightAmount: Int, val newScaleProbability: Int) {

  private val letters = Array('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

  val players: Array[Player] = Array.ofDim(playerAmount)

  var isOver = false

  val scales = Buffer[Scale]()

  var turn: Player = players(0)

  def addPlayer(symbol: Char, weightAmount: Int): Unit = {
    this.players(symbol.toInt - 97) = new Player(symbol, weightAmount)
  }

  def addScale(): Unit = {
    ???
  }

  def playTurn(player: Player): Unit = {
    ???
  }
}

