package ScaleGame

import math.abs
import scala.collection.mutable.Map

class Scale(val radius: Int, val symbol: Char) {

  val leftTiles: Array[Tile] = Array.ofDim(radius)

  val rightTiles: Array[Tile] = Array.ofDim(radius)

  def placeTiles(): Unit = {
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
    val allTiles = leftTiles ++ rightTiles
    val points = Map[Player, Int]()
    players.foreach( player => points += (player -> 0) )
    for(player <- players) {
      var sum = 0
      allTiles.foreach( tile =>
        if (tile.scale.isEmpty) sum += tile.pointsForPlayer(player)
        else sum += tile.distance * tile.scale.get.pointsPerPlayer(Array(player))(player)
      )
      points(player) = sum
    }
    points
  }
}
