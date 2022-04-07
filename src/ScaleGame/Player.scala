package ScaleGame

/**
 * Player objects represent the individual players of the game
 * Each player is identified uniquely with a color
 *
 * @param color the color of this player
 * @param weightAmount the initial amount of weights for this player
 */
class Player(val color: java.awt.Color, weightAmount: Int) {

  // Keeps track of how many weights this player has left
  var weightsLeft = weightAmount

  override def toString = {
    this.color match {
      case java.awt.Color.GREEN => "Green"
      case java.awt.Color.RED => "Red"
      case java.awt.Color.ORANGE => "Orange"
      case java.awt.Color.BLUE => "Blue"
    }
  }

}
