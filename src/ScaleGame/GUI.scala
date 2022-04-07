package ScaleGame

import scala.collection.mutable.Buffer
import scala.swing.BorderPanel.Position._
import scala.swing.event.{SelectionChanged, ValueChanged}
import java.awt.{Color, Font}
import scala.swing.{BorderPanel, Button, ComboBox, Dimension, Graphics2D, GridPanel, Label, MainFrame, Orientation, Panel, Rectangle, SimpleSwingApplication, Slider}
import scala.util.Random

object GUI extends SimpleSwingApplication {

  // Random for determining the radius of the first scale
  val random = new Random(System.nanoTime)

  // Initially holds all the possible players
  var players = Buffer(new Player(Color.GREEN, 20), new Player(Color.RED, 20), new Player(Color.ORANGE, 20), new Player(Color.BLUE, 20))

  // Is going to hold the game object
  var game: Game = null

  // Creates the first scale
  val firstScale = new Scale(random.nextInt(5) + 6, 'A')
  firstScale.placeTiles()

  // Keeps track of whose turn it is
  var turn = players.head

  // Becomes true if a scale gets out of balance
  var gotOutOfBalance = false

  // Becomes true if a player tries to place a weight on a tile that holds a scale
  var invalidInput = false

  // Builds the actual interface
  def top = new MainFrame {
    title = "Scale Game"

    // Holds the currently selected scale
    var currentScale = firstScale

    // Displays a list with all the scales that have been added to the game and listens to selections
    var scaleList: ComboBox[Char] = new ComboBox(List())

    // Updates the scale list so newly added scales get added to the list
    def updateScaleList() = {
      scaleList = new ComboBox(game.scales.map(_.symbol)) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {
          currentScale = game.scales.find(_.symbol == selection.item).get // Finds the scale based on the currently selected symbol and update currentScale
          distance.max = currentScale.radius // Updates the maximum value of the distance slider based on the radius of the currently selected scale
        }
      }
     }
    }

    // Holds the currently selected side
    var currentSide = 'L'

    // Holds the possible sides
    val sides = Array('L', 'R')

    // Displays a list with the possible sides and listens to selections
    var side = new ComboBox(sides) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {
          currentSide = selection.item // Updates currentSide based on the selection
        }
      }
    }

    // Holds the currently selected distance
    var currentDistance = 1

    // Displays a slider for choosing how far from the center a weight will be placed
    val distance = new Slider {
      orientation = Orientation.Horizontal
      min = 1
      max = currentScale.radius
      value = 1
      majorTickSpacing = 1
      minorTickSpacing = 1
      paintTicks = true
      paintLabels = true
      snapToTicks = true

      listenTo(this)

      reactions += {
        case e:ValueChanged => { currentDistance = this.value } // Saves the currently selected distance to currentDistance
      }

    }

    // Holds the currently selected player amount
    var currentPlayerAmount = 1

    // Displays a slider for choosing the player amount of the game
    val playerAmount = new Slider {
      orientation = Orientation.Horizontal
      min = 1
      max = 4
      value = 1
      majorTickSpacing = 1
      minorTickSpacing = 1
      paintTicks = true
      paintLabels = true
      snapToTicks = true

      listenTo(this)

      reactions += {
        case e:ValueChanged => { currentPlayerAmount = this.value } // Updates currentPlayerAmount based on the current selection
      }

    }

    // Holds the currently selected weight amount
    var currentWeightAmount = 1

    // Displays a slider for choosing the weight amount
    val weightAmount = new Slider {
      orientation = Orientation.Horizontal
      min = 1
      max = 20
      value = 1
      majorTickSpacing = 1
      minorTickSpacing = 1
      paintTicks = true
      paintLabels = true
      snapToTicks = true

      listenTo(this)

      reactions += {
        case e:ValueChanged => { currentWeightAmount = this.value } // Updates currentWeightAmount based on the currently selected value
      }

    }

    // Holds the currently selected new scale probability
    var currentNewScaleProbability = 0

    // Displays a slider for choosing the new scale probability
    val newScaleProbability = new Slider {
      orientation = Orientation.Horizontal
      min = 0
      max = 50
      value = 1
      majorTickSpacing = 5
      minorTickSpacing = 1
      paintTicks = true
      paintLabels = true
      snapToTicks = true

      listenTo(this)

      reactions += {
        case e:ValueChanged => { currentNewScaleProbability = this.value } // Updates currentNewScaleProbability based on the current selection
      }

    }

    // Updates the interface to display all the new changes
    def updateContent(): Unit = {

      this.updateScaleList()

      currentScale = firstScale // Resets the currently selected scale to the first scale to fix currentScale having a different value than the selection

      distance.max = firstScale.radius // Resets the maximum value of the distance slider to prevent it from displaying the radius of a wrong scale

      // Updates the the contents to display changes
      contents = new BorderPanel {
      layout += new GridPanel(2, 4) {
        contents += new Label("Select scale")
        contents += new Label("Select side")
        contents += new Label("Select distance")
        contents += new Label("Press to play turn")
        contents += scaleList
        contents += side
        contents += distance
        contents += submitButton
      } -> North
      layout += ScalePanel -> Center
     }
      size = new Dimension(1600, 1000)
    }

    // Removes the unnecessary elements from the interface after the game has ended
    def addEndScreen() = {
      contents = ScalePanel
    }

    // Displays a button that exits the start menu and initiates the actual game
    val startGame = Button("Start Game") {

      players = players.take(currentPlayerAmount) // Removes the unwanted players
      players.foreach(_.weightsLeft = currentWeightAmount) // Sets the weightsLeft for each player to the selected amount

      game = new Game(players.clone.toArray, currentNewScaleProbability) // Creates the actual game object
      game.scales += firstScale // Adds the first scale to the game

      this.updateContent()
    }

    // Displays a button that plays and ends the current player's turn (except in case of an invalid input in which the player gets to try again)
    val submitButton = Button("Play turn") {

      invalidInput = false
      gotOutOfBalance = false

      // Determines if the input was invalid or not
       if (currentSide == 'L') {
          if (currentScale.leftTiles(currentDistance - 1).scale.isEmpty) {
            gotOutOfBalance = game.playTurn(turn, currentScale, currentSide, currentDistance) // gotOutOfBalance becomes true if a scale got out of balance after the weight was placed

            // Changse the value of the turn variable to the next player
            players.dropInPlace(1)
            if (players.nonEmpty) {
              turn = players.head
            } else {
              players = game.players.toBuffer
              turn = players.head
            }

          } else {
            invalidInput = true // Changes invalidInput to true if the player tried to place the weight on a tile that holds a scale
          }
        } else {
          if (currentScale.rightTiles(currentDistance - 1).scale.isEmpty) {
            gotOutOfBalance = game.playTurn(turn, currentScale, currentSide, currentDistance)

            players.dropInPlace(1)

            if (players.nonEmpty) {
              turn = players.head
            } else {
              players = game.players.toBuffer
              turn = players.head
            }
          } else {
            invalidInput = true
          }
        }

      // Displays the end screen if all players have run out of weights, otherwise just updates the interface
      if (!game.isOver) this.updateContent() else this.addEndScreen()
    }

    // Contains the panel that displays the state of the game (scales, points...)
    val ScalePanel = new Panel {

      val width = 1600
      val height = 900
      val squareWidth = 20 // The width of each square the scales consist of

      // Paints each component in the panel
      override def paintComponent(g: Graphics2D): Unit = {

        /**
         * Paints one scale and the weights that are on top of it
         * If there is another scale on top of the scale, calls paintScale on that scale thus painting it aswell
         *
         * @param scale the scale that is to be painted
         * @param x The x coordinate of the scale
         * @param y The y coordinate of the scale
         * @param height The height of the leg of the scale
         */
        def paintScale(scale: Scale, x: Int, y: Int, height: Int): Unit = {
          g.setColor(Color.BLACK)

          // Holds the center square of the platform of the scale
          var upperRect = new Rectangle(x, y, this.squareWidth, this.squareWidth)

          // Draws the leg of the scale
          for (i <- 0 to height) {
            val newRect = new Rectangle(x, y - i * squareWidth, this.squareWidth, this.squareWidth)
            g.fill(newRect)
            upperRect = newRect
          }

          // Draws the symbol on the scale
          g.setColor(Color.WHITE)
          g.drawString(scale.symbol.toString, upperRect.x + this.squareWidth / 3, upperRect.y + this.squareWidth)
          g.setColor(Color.BLACK)

          // Paints the left tiles of the scale
          for (tile <- scale.leftTiles) {
            g.setColor(Color.BLACK)
            val rect = new Rectangle(upperRect.x - tile.distance * squareWidth, upperRect.y , this.squareWidth, this.squareWidth)
            g.fill(rect)
            g.setColor(Color.RED)
            g.draw(rect)

            // If the tile holds a scale, calls paintScale on it
            // If the tile holds weights, they're painted aswell
            if (tile.scale.nonEmpty) {
              paintScale(tile.scale.get, rect.x, rect.y - this.squareWidth, math.max(scale.leftHeight() + 1, 2))
            } else if (tile.weights.nonEmpty) {
              for (i <- 1 to tile.weights.length) {
                g.setColor(tile.weights.head.owner.color)
                g.fillOval(rect.x, rect.y - i *  this.squareWidth, this.squareWidth, this.squareWidth)
              }
            }

          }

          // Paints the right tiles of the scale
          for (tile <- scale.rightTiles) {
            g.setColor(Color.BLACK)
            val rect = new Rectangle(upperRect.x + tile.distance * squareWidth, upperRect.y , this.squareWidth, this.squareWidth)
            g.fill(rect)
            g.setColor(Color.RED)
            g.draw(rect)

            if (tile.scale.nonEmpty) {
              paintScale(tile.scale.get, rect.x, rect.y - this.squareWidth, math.max(scale.rightHeight() + 1, 2))
            } else if (tile.weights.nonEmpty) {
              for (i <- 1 to tile.weights.length) {
                g.setColor(tile.weights.head.owner.color)
                g.fillOval(rect.x, rect.y - i *  this.squareWidth, this.squareWidth, this.squareWidth)
              }
            }
          }
        }

        // Paints the lowest scale of the game, thus recursively painting all of them
        paintScale(game.scales.head, this.width / 2, this.height - 100, 2 )

        // Calculates the points for each player and stores them in a variable
        val points = game.scales.head.pointsPerPlayer(game.players).toArray

        g.setColor(Color.BLACK)
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20))
        g.drawString("Points:", 50, this.height - 185)
        g.drawString("Weights left:", 225, this.height - 185)
        if (!game.isOver) g.drawString("Turn: " + turn, this.width / 2 - 90, this.height - 50) // Paint the game over text if the game is over, otherwise display whose turn it is

        // Paints the points of each player
        for (player <- points) {
          g.setColor(player._1.color)
          g.drawString(player._1 + ": " + player._2, 50, points.indexOf(player) * 20 + this.height - 150)
          g.drawString(player._1 + ": " + player._1.weightsLeft, 225, points.indexOf(player) * 20 + this.height - 150)
        }

        // If an invalid input was given, displays a text informing the player of that
        if (invalidInput) {
          g.setColor(Color.RED)
          g.drawString("That tile has a scale on it", this.width - 400, this.height - 50)
        }

        // If a scale got out of balance, displays a text informing the player of that
        if (gotOutOfBalance) {
          g.setColor(Color.RED)
          g.drawString("Scale got out of balance!", this.width - 400, this.height - 50)
        }

        // If the game is over, determines the winner of the game and displays that player's color
        if (game.isOver) {
          g.setColor(Color.RED)
          g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 40))
          g.drawString("Game over!", this.width / 2 - 90, 70)
          val winner = points.maxBy(_._2)
          g.setColor(Color.BLACK)
          if (points.exists(player => player._2 == winner._2 && player._1 != winner._1)) {
            g.drawString("It's a tie!", this.width / 2 - 100, this.height - 40)
          } else {
            g.drawString(winner._1 + " wins!", this.width / 2 - 100, this.height - 40)
          }
        }

      }
      preferredSize = new Dimension(this.width, this.height)
    }

    // Sets the content to the start menu
    contents = new GridPanel(7, 1) {
      contents += new Label("Select player amount")
      contents += playerAmount
      contents += new Label("Select new scale probability")
      contents += newScaleProbability
      contents += new Label("Select weight amount")
      contents += weightAmount
      contents += startGame
    }

    size = new Dimension(720, 1000)
    centerOnScreen()
  }
}
