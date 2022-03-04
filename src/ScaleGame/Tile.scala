package ScaleGame

import scala.collection.mutable.Buffer

class Tile(val distance: Int) {

  var weights = Buffer[Weight]()

  var scale: Option[Scale] = None

  def totalWeight(): Int = {
    if (this.scale.isEmpty) this.distance * this.weights.length else this.distance * this.scale.get.totalWeight()
  }

  def pointsForPlayer(player: Player): Int = {
    try {
      if (this.weights.head.owner == player) this.distance * this.weights.length else 0
    } catch {
      case e:IndexOutOfBoundsException => 0
    }
  }

  override def toString = s"${this.distance}, ${this.weights}, ${this.scale}"


}
