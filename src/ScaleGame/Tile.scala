package ScaleGame

import scala.collection.mutable.Buffer

class Tile(val distance: Int) {

  val weights = Buffer[Weight]()

  var isEmpty = true

  var scale: Option[Scale] = None

  def totalWeight(): Int = {
    ???
  }

  def pointsForPlayer(player: Player): Int = {
    ???
  }
}
