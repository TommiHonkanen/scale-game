package ScaleGame
import scala.collection.mutable.Buffer
import scala.io.StdIn.{readChar, readInt, readLine}

object UI extends App {

  def startGame(): Unit = {

    var playerAmount = 0

    while (!(playerAmount <= 4 && playerAmount >= 1)) {
      print("Enter player amount (1-4): ")
      playerAmount = readInt()
    }

    var weightAmount = 0

    while (!(weightAmount <= 20 && weightAmount >= 3)) {
      print("Enter weight amount (3-20): ")
      weightAmount = readInt()
    }

    val players: Array[Player] = Array.ofDim(playerAmount)

    for (i <- 0 until playerAmount) {
      players(i) = new Player((97 + i).toChar, weightAmount)
      println(s"Player ${i + 1} symbol: ${(97 + i).toChar}")
    }

    var newScaleProbability = -1

    while (!(newScaleProbability <= 100 && newScaleProbability >= 0)) {
      print("Enter new scale probability (0 - 100): ")
      newScaleProbability = readInt()
    }

    this.playGame(new Game(players, newScaleProbability))
  }

  def playGame(g: Game) = {
    val game = g

    var players = game.players.toBuffer

    var turn: Player = players.head

    while (!game.isOver) {

      this.drawGame()

      println("Turn: " + turn)

      var scaleSymbol = '.'

      while (!game.scales.exists(_.symbol == scaleSymbol)) {
        print("Enter scale symbol: ")
        scaleSymbol = readChar()
      }

      val scale = game.scales.find(_.symbol == scaleSymbol).get

      var side = '.'

      while (side != 'L' || side != 'R') {
        print("Enter side (L/R): ")
        side = readChar()
      }

      var position = 0

      while (position < 1 || position > scale.radius) {
        print(s"Enter position ${0 - scale.radius}: ")
        position = readInt()
      }

      game.playTurn(turn, scale, side, position)

      players.dropInPlace(1)
      if (players.nonEmpty) {
        turn = players.head
      } else {
        players = game.players.toBuffer
        turn = players.head
      }
    }
  }

  private def drawGame() = {
    ???
  }

  private def drawScale(verticalDisplacement: Int, height: Int, scale: Scale) = {
    val allTiles = scale.leftTiles ++ scale.rightTiles
    val weightHeight = allTiles.maxBy(_.weights.length).weights.length

    val scaleLevels: Buffer[Array[String]] = Buffer[Array[String]]()

    val platform = Buffer[String]()

    platform += "<"

    for (i <- scale.radius to 1 by -1) {
      platform += i.toString
      platform += "="
    }

    platform += scale.symbol.toString

    for (i <- 1 to scale.radius) {
      platform += "="
      platform += i.toString
    }

    platform += ">"
    scaleLevels += platform.toArray

    for (i <- 0 until weightHeight) {

      var weights: Array[String] = Array.ofDim(platform.length)
      weights = weights.map(j => " ")

      for (j <- scale.leftTiles) {
        if (j.weights.length > i) {
          weights(platform.indexOf(j.distance.toString)) = j.weights(i).owner.symbol.toString
        }
      }
      for (j <- scale.rightTiles) {
        if (j.weights.length > i) {
          weights(platform.indexOf(j.distance.toString) + 4 * j.distance) = j.weights(i).owner.symbol.toString
        }
      }
      scaleLevels.prepend(weights)
    }

    for (i <- 1 to height) {

      var array: Array[String] = Array.ofDim(platform.length)
      array = array.map(j => " ")

      array(array.length / 2) = "*"

      scaleLevels += array

    }
    scaleLevels
  }

  startGame()
}
