package ScaleGame

import scala.collection.mutable.Buffer

class Tile(val distance: Int) {

  val weights = Buffer[Weight]()

  var scale: Option[Scale] = None

  def totalWeight(): Int = {
    if (this.scale.isEmpty) this.weights.length else this.scale.get.totalWeight()
  }

  def pointsForPlayer(player: Player): Int = {
    if (this.weights.head.owner == player) this.weights.length else 0
  }
}
