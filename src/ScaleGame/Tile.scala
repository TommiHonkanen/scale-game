package ScaleGame

import scala.collection.mutable.Buffer

/**
 * Each scale consists of tile objects
 * Tile objects keep track of the weights and the scale that could be on top of it
 *
 * @param distance the distance of this tile from the center of the scale
 */
class Tile(val distance: Int) {

  // Holds the weights on this tile
  var weights = Buffer[Weight]()

  // Holds the scale on this tile
  var scale: Option[Scale] = None

  // Calculates the total weight on this tile
  def totalWeight(): Int = {
    if (this.scale.isEmpty) this.distance * this.weights.length
    else this.distance * this.scale.get.totalWeight()
  }

  // Calculates the points that the player given as a parameter gets from the weights on this tile
  def pointsForPlayer(player: Player): Int = {
    try {
      if (this.weights.head.owner == player) this.distance * this.weights.length else 0
    } catch {
      case e:IndexOutOfBoundsException => 0
    }
  }

}
