package ScaleGame

import scala.collection.mutable.Buffer
import scala.util.Random

/**
 * Each game is represented by a game object
 * It keeps track of all the scales and handles major functions of the game
 *
 * @param players an array containing the players that take a part in this game
 * @param newScaleProbability the probability of a new scale spawning after a round
 */
class Game (val players: Array[Player], val newScaleProbability: Int) {

  // Holds the symbol that will be used for the next new scale
  private var symbol = 'B'

  // Becomes true when every player has used all weights
  var isOver = false

  // Holds all the scales that are currently in the game
  val scales = Buffer[Scale]()

  // Adds a new scale on a random scale that is already in the game
  // Each scale can hold at most two scales, one on the left side and one on the right side
  private def addScale(): Unit = {

    // Random for choosing a random scale in addScale
    val random = new Random(System.nanoTime)

    // Finds a scale that doesn't already have a scale on both sides and has a free tile
    val chosenScale = random.shuffle(scales).find( scale =>
      (scale.radius != 2 && scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty) && scale.rightTiles(0).scale.isEmpty) ||
      (scale.radius != 2 && scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty) && scale.leftTiles(0).scale.isEmpty) ||
      // If the scale has a radius of one, it can only have a scale on one side
      (scale.radius == 2 && scale.leftTiles.forall(_.scale.isEmpty) && scale.rightTiles.forall(_.scale.isEmpty) && (scale.leftTiles.exists(_.weights.isEmpty) || scale.rightTiles.exists(_.weights.isEmpty)))
    )

    // Only continues if a scale was found
    if (chosenScale.isDefined) {

      val scale = chosenScale.get

      val leftScale = scale.leftTiles(0).scale

      val rightScale = scale.rightTiles(0).scale

      var chosenTile: Option[Tile] = None

      // Finds a random free tile on which to place the scale and assigns it to chosenTile
      if (scale.radius > 2) {
        if (random.nextInt(2) == 1) {
          if (scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) {
            chosenTile = Some(random.shuffle(scale.leftTiles.toBuffer).find(tile => (tile.weights.isEmpty && (tile.distance != 1))).get)
          } else {
            chosenTile = Some(random.shuffle(scale.rightTiles.toBuffer).find(tile => (tile.weights.isEmpty && (tile.distance != 1))).get)
          }
        } else {
          if (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty)) {
            chosenTile = Some(random.shuffle(scale.rightTiles.toBuffer).find(tile => (tile.weights.isEmpty && (tile.distance != 1))).get)
          } else {
            chosenTile = Some(random.shuffle(scale.leftTiles.toBuffer).find(tile => (tile.weights.isEmpty && (tile.distance != 1))).get)
          }
        }
      } else {
        if (random.nextInt(2) == 1) {
          if (scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) {
            chosenTile = Some(random.shuffle(scale.leftTiles.toBuffer).find(tile => tile.weights.isEmpty).get)
          } else {
            chosenTile = Some(random.shuffle(scale.rightTiles.toBuffer).find(tile => tile.weights.isEmpty).get)
          }
        } else {
          if (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty)) {
            chosenTile = Some(random.shuffle(scale.rightTiles.toBuffer).find(tile => tile.weights.isEmpty).get)
          } else {
            chosenTile = Some(random.shuffle(scale.leftTiles.toBuffer).find(tile => tile.weights.isEmpty).get)
          }
        }
      }

      // Only continues if a tile was found
      if (chosenTile.isDefined) {

        val tile = chosenTile.get

        // Calculates a radius for the new scale, it can't be larger than the distance variable of the chosen tile to avoid collisions
        val radius = Math.max(random.nextInt(tile.distance), 2) // Math.max(tile.distance - 1, 2)

        // Creates the new scale
        val newScale = new Scale(radius, symbol)
        newScale.placeTiles()

        // Adds the new scale to the game
        tile.scale = Option(newScale)
        this.scales += newScale
      }
    }
  }

  /**
   * Plays a turn in the game by placing the weight and determining if the scales got out of balance or not
   *
   * @param player the player whose turn it is
   * @param scale the scale that the weight will be attempted to placed on
   * @param side the side of the scale that the weight will be placed on ('L' or 'R')
   * @param position the distance from the center of the scale that the weight will be placed on
   * @return true if a scale got out of balance and false if the weight was placed successfully
   */
  def playTurn(player: Player, scale: Scale, side: Char, position: Int): Boolean = {

    // Random for choosing the radius of the potential new scale
    val random = new Random(System.nanoTime)

    // Becomes true if the scales got out of balance
    var failed = false

    var originalWeights = Buffer[Weight]()

    // Save a copy of the potential weights already on the chosen tile into originalWeights
    if (side == 'L') {
        originalWeights = scale.leftTiles(position - 1).weights.clone()
      } else {
        originalWeights = scale.rightTiles(position - 1).weights.clone()
      }

      var originalOwner: Player = null

      // Determine if the tile already has weights and store the owner of those weights into originalOwner
      if (originalWeights.nonEmpty) {
        originalOwner = originalWeights.head.owner
      }

      // Attempt to place the weight
      scale.placeWeight(side, position, player)

      // If the scales got out of balance, remove the weight that was placed and restore the owner of those weights
      if (this.scales.exists(!_.isBalanced())) {
        failed = true
        if (side == 'L') {
          scale.leftTiles(position - 1).weights = originalWeights
          if (originalWeights.nonEmpty) {
            scale.leftTiles(position - 1).weights.foreach(_.owner = originalOwner)
          }
        } else {
          scale.rightTiles(position - 1).weights = originalWeights
          if (originalWeights.nonEmpty) {
            scale.rightTiles(position - 1).weights.foreach(_.owner = originalOwner)
          }
        }
      }

    // Decrease the number of weights the player has left by one regardless of if the placing of the weight was successful or not
    player.weightsLeft -= 1

    // Determine if a new scale will be added
    if ((random.nextInt(100) + 1 <= this.newScaleProbability) && (player == players.last)) {
      this.addScale()
      symbol = (symbol + 1).toChar
    }

    // Determine if all players have run out of weights
    if (this.players.forall(_.weightsLeft == 0)) this.isOver = true

    failed
  }
}

