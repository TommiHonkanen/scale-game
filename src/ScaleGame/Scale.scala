package ScaleGame

import math.{abs, max}
import scala.collection.mutable.Map

/**
 * Each scale is represented by a scale object
 * Each scale keeps track of its tiles and takes care of weight and point calculation
 *
 * @param radius the amount of tiles on each side of the scale
 * @param symbol the unique symbol of the scale
 */
class Scale(val radius: Int, val symbol: Char) {

  // Holds the tiles on the left side of the scale
  var leftTiles: Array[Tile] = Array.ofDim(radius)

  // Holds the tiles on the right side of the scale
  var rightTiles: Array[Tile] = Array.ofDim(radius)

  // Adds Tile-objects to the arrays
  def placeTiles(): Unit = {
    for (i <- 1 to radius) {
      leftTiles(i - 1) = new Tile(i)
      rightTiles(i - 1) = new Tile(i)
    }
  }

  // Calculates the total weight of the scale using recursion
  def totalWeight(): Int = {
    val allTiles = leftTiles ++ rightTiles
    var weight = 0

    for (tile <- allTiles) {
      if (tile.scale.isDefined) {
        weight += tile.scale.get.totalWeight() // If the current tile contains a scale, call this method on that scale and add it to the weight of this scale
      } else {
        weight += tile.weights.length // Otherwise the weight on that tile is just the length of that tile's weight array
      }
    }
    weight
  }

  // Calculates the total weight on the left side of the scale
  private def leftWeight(): Int = {
    leftTiles.map(_.totalWeight()).sum
  }

  // Calculates the total weight on the right side of the scale
  private def rightWeight(): Int = {
    rightTiles.map(_.totalWeight()).sum
  }

  // Determines if the scale is in equilibrium
  def isBalanced(): Boolean = {
    abs(this.leftWeight() - this.rightWeight()) <= this.radius
  }

  /**
   *  Places a new weight on this scale using the given parameters
   *
   * @param side the side that the weight shall be placed on ('L' or 'R')
   * @param distance the distance from the center that the weight will be placed on
   * @param player the player that will own the new weight
   */
  def placeWeight(side: Char, distance: Int, player: Player): Unit = {
    if (side == 'L') {
      leftTiles(distance - 1).weights += new Weight(player)
      leftTiles(distance - 1).weights.foreach(_.owner = player) // Sets the owner of all the weights on that tile to player
    } else {
      rightTiles(distance - 1).weights += new Weight(player)
      rightTiles(distance - 1).weights.foreach(_.owner = player)
    }
  }

  // Places the current maximum height of weights on the left side of the scale
  private def leftHeight() = {
    leftTiles.maxBy(_.weights.length).weights.length
  }

  // Places the current maximum height of weights on the right side of the scale
  private def rightHeight() = {
    rightTiles.maxBy(_.weights.length).weights.length
  }

  def weightHeight() = {
    max(this.leftHeight(), this.rightHeight())
  }

  /**
   * Calculates total points on this scale for the players given in the parameter array using recursion
   *
   * @param players an array containing the players whose points will be calculated
   * @return a Map in which each player in the players array is mapped to that player's total points on this scale
   */
  def pointsPerPlayer(players: Array[Player]): Map[Player, Int] = {

    // Combines the tile arrays into one single array
    val allTiles = leftTiles ++ rightTiles

    // Mutable Map that holds the Map that will be returned
    val points = Map[Player, Int]()

    // Sets every player's points to 0
    players.foreach( player => points += (player -> 0) )

    // Loops over the players and calculates the points for each player
    for(player <- players) {

      // Holds the points, initially 0
      var sum = 0

      // Loops over all the tiles on this scale
      allTiles.foreach( tile =>

        // If the tile doesn't contain a scale, just calculate the points for that player on that tile and add it to sum
        if (tile.scale.isEmpty) sum += tile.pointsForPlayer(player)

        // If the tile contains a scale, call this method on that scale and add the points on that scale (multiplied by the distance of this tile) to sum
        else sum += tile.distance * tile.scale.get.pointsPerPlayer(Array(player))(player)
      )

      // Set the points for the current player
      points(player) = sum
    }
    points
  }
}
