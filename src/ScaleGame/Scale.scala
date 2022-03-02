package ScaleGame

class Scale(val radius: Int, val symbol: Char) {

  val leftTiles = Array.ofDim(radius)

  val rightTiles = Array.ofDim(radius)

  private def placeTiles(): Unit = {
    ???
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

  }

  def pointsPerPlayer(): Map[Player, Int] = {
    ???
  }

}
