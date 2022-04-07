package ScaleGame

import scala.collection.mutable.Buffer
import scala.util.Random

class Game (val players: Array[Player], val newScaleProbability: Int) {

  var random = new Random(System.nanoTime)

  private val letters = Buffer('B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')

  var isOver = false

  val scales = Buffer[Scale]()

  def addScale(): Unit = {

    val random = new Random(System.nanoTime)

    /*
    val chosenScale = this.random.shuffle(scales).find( scale =>
      (scale == this.scales.head && ((scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) || (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty)))) ||
      ((scale.leftTiles.forall(_.scale.isEmpty) && scale.rightTiles.forall(_.scale.isEmpty)) && (scale.leftTiles.exists(_.weights.isEmpty) || scale.rightTiles.exists(_.weights.isEmpty)))
    )

    if (chosenScale.isDefined) {

    val scale = chosenScale.get

    var chosenTile = scale.leftTiles.head

      if (random.nextInt(2) == 1) {
        if (scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) {
          chosenTile = this.random.shuffle(scale.leftTiles.toBuffer).find(_.weights.isEmpty).get
        } else {
          chosenTile = this.random.shuffle(scale.rightTiles.toBuffer).find(_.weights.isEmpty).get
        }
      } else {
        if (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty)) {
          chosenTile = this.random.shuffle(scale.rightTiles.toBuffer).find(_.weights.isEmpty).get
        } else {
          chosenTile = this.random.shuffle(scale.leftTiles.toBuffer).find(_.weights.isEmpty).get
        }
      }
      */

      val chosenScale = this.random.shuffle(scales).find( scale =>
      (scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) ||
      (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty))
    )

    if (chosenScale.isDefined) {

    val scale = chosenScale.get

    var chosenTile = scale.leftTiles.head

      if (random.nextInt(2) == 1) {
        if (scale.leftTiles.forall(_.scale.isEmpty) && scale.leftTiles.exists(_.weights.isEmpty)) {
          chosenTile = this.random.shuffle(scale.leftTiles.toBuffer).find(_.weights.isEmpty).get
        } else {
          chosenTile = this.random.shuffle(scale.rightTiles.toBuffer).find(_.weights.isEmpty).get
        }
      } else {
        if (scale.rightTiles.forall(_.scale.isEmpty) && scale.rightTiles.exists(_.weights.isEmpty)) {
          chosenTile = this.random.shuffle(scale.rightTiles.toBuffer).find(_.weights.isEmpty).get
        } else {
          chosenTile = this.random.shuffle(scale.leftTiles.toBuffer).find(_.weights.isEmpty).get
        }
      }


      val symbol = this.letters.head
      this.letters.dropInPlace(1)

      val radius = Math.max(random.nextInt(chosenTile.distance), 1)

      val newScale = new Scale(radius, symbol)

      newScale.placeTiles()

      chosenTile.scale = Option(newScale)

      this.scales += newScale



    }


    /*


     */


    /*


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
              tile.scale.get.placeTiles()
              found = true
            }
          )
        } else {
          scale.rightTiles.foreach( tile =>
            if (tile.weights.isEmpty && tile.scale.isEmpty) {
              tile.scale = Some(new Scale(this.random.nextInt(10) + 1, symbol))
              tile.scale.get.placeTiles()
              found = true
            }
          )
        }
      }
    }
    */
  }

  def playTurn(player: Player, scale: Scale, side: Char, position: Int): Boolean = {

    var failed = false

    var originalWeights = Buffer[Weight]()


    if (side == 'L') {
        originalWeights = scale.leftTiles(position - 1).weights.clone()
      } else {
        originalWeights = scale.rightTiles(position - 1).weights.clone()
      }

      var originalOwner: Player = null

      if (originalWeights.nonEmpty) {
        originalOwner = originalWeights.head.owner
      }

      scale.placeWeight(side, position, player)

      if (this.scales.exists(!_.isBalanced())) {
        println("Scale got out of balance!")
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

    player.weightsLeft -= 1

    if (random.nextInt(100) + 1 <= this.newScaleProbability) this.addScale()

    if (this.players.forall(_.weightsLeft == 0)) this.isOver = true

    failed
  }
}

