package ScaleGame

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
    ???
  }

  private def leftWeight(): Int = {
    ???
  }

  private def rightWeight(): Int = {
    ???
  }

  def isBalanced(): Boolean = {
    ???
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

  def pointsPerPlayer(): Map[Player, Int] = {
    ???
  }

}
