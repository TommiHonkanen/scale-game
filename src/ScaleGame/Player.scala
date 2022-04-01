package ScaleGame

import java.awt.Color

class Player(val color: java.awt.Color, weightAmount: Int) {

  var weightsLeft = weightAmount

  var points: Int = 0

  override def toString = {
    this.color match {
      case java.awt.Color.GREEN => "Green"
      case java.awt.Color.RED => "Red"
      case java.awt.Color.ORANGE => "Orange"
      case java.awt.Color.BLUE => "Blue"
    }

  }

}
