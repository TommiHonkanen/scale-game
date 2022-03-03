package ScaleGame
import scala.io.StdIn.{readLine, readChar, readInt}

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

  def playGame(game: Game) = {
    ???
  }

  private def drawGame() = {
    ???
  }

  private def drawScale() = {
    ???
  }

  startGame()
}
