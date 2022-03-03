package ScaleGame

import math.abs

class Scale(val radius: Int, val symbol: Char) {

  val leftTiles: Array[Tile] = Array.ofDim(radius)

  val rightTiles: Array[Tile] = Array.ofDim(radius)

  private def placeTiles(): Unit = {
    for (i <- 1 to radius) {
      leftTiles(i - 1) = new Tile(i)
      rightTiles(i - 1) = new Tile(i)
    }
  }

  def totalWeight(): Int = {
    this.leftWeight() + this.rightWeight()
  }

  private def leftWeight(): Int = {
    leftTiles.map(_.totalWeight()).sum
  }

  private def rightWeight(): Int = {
    rightTiles.map(_.totalWeight()).sum
  }

  def isBalanced(): Boolean = {
    abs(this.leftWeight() - this.rightWeight()) <= this.radius
  }

  def placeWeight(side: Char, distance: Int, player: Player): Unit = {
    if (side == 'L') {
      leftTiles(distance - 1).weights += new Weight(player)
      leftTiles(distance - 1).weights.foreach(_.owner = player)
    } else {
      rightTiles(distance - 1).weights += new Weight(player)
      rightTiles(distance - 1).weights.foreach(_.owner = player)
    }
  }

  def pointsPerPlayer(players: Array[Player]): Map[Player, Int] = {
    ???
  }

}
