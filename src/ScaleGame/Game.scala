package ScaleGame

import scala.collection.mutable.Buffer
import scala.util.Random

class Game (val players: Array[Player], val newScaleProbability: Int) {

  private val random = new Random

  private val letters = Buffer('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

  var isOver = false

  val scales = Buffer[Scale]()

  def addPlayer(symbol: Char, weightAmount: Int): Unit = {
    this.players(symbol.toInt - 97) = new Player(symbol, weightAmount)
  }

  def addScale(): Unit = {
    val symbol = this.letters.head
    this.letters.dropInPlace(1)

    if (scales.isEmpty) {
      scales += new Scale(this.random.nextInt(10) + 1, symbol)
    } else {
      val scale = this.random.shuffle(scales)
                             .find(scale => (scale.leftTiles
                             .exists(tile => tile.weights.isEmpty && tile.scale.isEmpty) || (scale.rightTiles
                             .exists(tile => tile.weights.isEmpty && tile.scale.isEmpty)))).get
      val side = if (this.random.nextInt(2) == 0) 'L' else 'R'
      var found = false

      while (!found) {
        if (side == 'L') {
          scale.leftTiles.foreach( tile =>
            if (tile.weights.isEmpty && tile.scale.isEmpty) {
              tile.scale = Some(new Scale(this.random.nextInt(10) + 1, symbol))
              found = true
            }
          )
        } else {
          scale.rightTiles.foreach( tile =>
            if (tile.weights.isEmpty && tile.scale.isEmpty) {
              tile.scale = Some(new Scale(this.random.nextInt(10) + 1, symbol))
              found = true
            }
          )
        }
      }
    }
  }

  def playTurn(player: Player, scale: Scale, side: Char, position: Int): Unit = {

    var originalWeights = Buffer[Weight]()

    if (side == 'L') {
      originalWeights = scale.leftTiles(position - 1).weights.clone()
    } else {
      originalWeights = scale.rightTiles(position - 1).weights.clone()
    }

    scale.placeWeight(side, position, player)

    if (this.scales.exists(!_.isBalanced())) {
      println("Scale got out of balance!")
      if (side == 'L') {
        scale.leftTiles(position - 1).weights = originalWeights
      } else {
        scale.rightTiles(position - 1).weights = originalWeights
      }
    }

    player.weightsLeft -= 1

    if (random.nextInt(100) + 1 <= this.newScaleProbability) this.addScale()
  }
}

