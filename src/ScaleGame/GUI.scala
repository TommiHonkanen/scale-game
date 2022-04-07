package ScaleGame

import scala.collection.mutable.Buffer
import scala.swing.BorderPanel.Position._
import scala.swing.event.{ButtonClicked, SelectionChanged, ValueChanged}
import java.awt.{BasicStroke, Color, Font}
import scala.swing.{BorderPanel, Button, ComboBox, Dimension, Frame, Graphics2D, GridPanel, Label, ListView, MainFrame, Orientation, Panel, Rectangle, SimpleSwingApplication, Slider, SplitPane}

object GUI extends SimpleSwingApplication {

  var players = Buffer(new Player(Color.GREEN, 20), new Player(Color.RED, 20), new Player(Color.ORANGE, 20), new Player(Color.BLUE, 20))

  var game: Game = new Game(players.toArray, 20)

  val firstScale = new Scale(game.random.nextInt(5) + 6, 'A')

  firstScale.placeTiles()

  game.scales += firstScale

  var turn = players.head

  var gameStarted = false

  var gotOutOfBalance = false

  var invalidInput = false

  def top = new MainFrame {
    title = "Scale Game"

    // val freeScales = game.scales.filter(scale => scale.leftTiles.exists(_.scale.isEmpty) || scale.rightTiles.exists(_.scale.isEmpty)).map(_.symbol)

    var currentScale = firstScale

    var scaleList = new ComboBox(game.scales.map(_.symbol)) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {

          currentScale = game.scales.find(_.symbol == selection.item).get
          distance.max = currentScale.radius

        }
      }
    }

    def updateScaleList() = {
      scaleList = new ComboBox(game.scales.map(_.symbol)) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {
          currentScale = game.scales.find(_.symbol == selection.item).get
          distance.max = currentScale.radius
        }
      }
     }
    }

    var currentSide = 'L'

    val sides = Array('L', 'R')


    var side = new ComboBox(sides) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {

          currentSide = selection.item

        }
      }
    }

    var currentDistance = 1

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
        case e:ValueChanged => { currentDistance = this.value }
      }

    }

    var currentPlayerAmount = 1

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
        case e:ValueChanged => { currentPlayerAmount = this.value }
      }

    }

    var currentWeightAmount = 1

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
        case e:ValueChanged => { currentWeightAmount = this.value }
      }

    }

    var currentNewScaleProbability = 0

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
        case e:ValueChanged => { currentNewScaleProbability = this.value }
      }

    }

    def updateContent(): Unit = {

      this.updateScaleList()

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
    }

    def addEndScreen() = {
      contents = ScalePanel
    }

    val startGame = Button("Start Game") {
      gameStarted = true

      this.updateContent()

      size = new Dimension(1600, 1000)

      players = players.take(currentPlayerAmount)

      players.foreach(_.weightsLeft = currentWeightAmount)

      game = new Game(players.clone.toArray, currentNewScaleProbability)

      game.scales += firstScale
    }

    val submitButton = Button("Play turn") {

       if (currentSide == 'L') {
          if (currentScale.leftTiles(currentDistance - 1).scale.isEmpty) {
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

      if (!game.isOver) this.updateContent() else this.addEndScreen()




      ScalePanel.visible = false

      ScalePanel.repaint

      ScalePanel.visible = true
    }

    val ScalePanel = new Panel {

      val width = 1600
      val height = 900
      val squareWidth = 20

      override def paintComponent(g: Graphics2D): Unit = {

        def paintScale(scale: Scale, x: Int, y: Int, height: Int): Unit = {
          g.setColor(Color.BLACK)
          var upperRect = new Rectangle(x, y, this.squareWidth, this.squareWidth)

          for (i <- 0 to height) {
            val newRect = new Rectangle(x, y - i * squareWidth, this.squareWidth, this.squareWidth)
            g.fill(newRect)
            upperRect = newRect
          }

          g.setColor(Color.WHITE)
          g.drawString(scale.symbol.toString, upperRect.x + this.squareWidth / 3, upperRect.y + this.squareWidth)
          g.setColor(Color.BLACK)

          val leftTileSquares: Buffer[Rectangle] = Buffer()
          val rightTileSquares: Buffer[Rectangle] = Buffer()

          for (tile <- scale.leftTiles) {
            // g.setStroke(new BasicStroke(1))
            g.setColor(Color.BLACK)
            val rect = new Rectangle(upperRect.x - tile.distance * squareWidth, upperRect.y , this.squareWidth, this.squareWidth)
            g.fill(rect)
            g.setColor(Color.RED)
            g.draw(rect)
            leftTileSquares += rect

            if (tile.scale.nonEmpty) {
              paintScale(tile.scale.get, rect.x, rect.y - this.squareWidth, math.max(scale.leftHeight() + 1, 2))
            } else if (tile.weights.nonEmpty) {
              for (i <- 1 to tile.weights.length) {
                g.setColor(tile.weights.head.owner.color)
                g.fillOval(rect.x, rect.y - i *  this.squareWidth, this.squareWidth, this.squareWidth)
              }
            }

          }

          for (tile <- scale.rightTiles) {
            g.setColor(Color.BLACK)
            val rect = new Rectangle(upperRect.x + tile.distance * squareWidth, upperRect.y , this.squareWidth, this.squareWidth)
            g.fill(rect)
            g.setColor(Color.RED)
            g.draw(rect)
            rightTileSquares += rect

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

        paintScale(game.scales.head, this.width / 2, this.height - 100, 2 )


        val points = game.scales.head.pointsPerPlayer(game.players).toArray

        g.setColor(Color.BLACK)
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20))
        g.drawString("Points:", 50, this.height - 185)
        g.drawString("Weights left:", 225, this.height - 185)
        if (!game.isOver) g.drawString("Turn: " + turn, this.width / 2 - 90, this.height - 50)

        for (player <- points) {
          g.setColor(player._1.color)
          g.drawString(player._1 + ": " + player._2, 50, points.indexOf(player) * 20 + this.height - 150)
          g.drawString(player._1 + ": " + player._1.weightsLeft, 225, points.indexOf(player) * 20 + this.height - 150)
        }


        if (invalidInput) {
          g.setColor(Color.RED)
          g.drawString("That tile has a scale on it", this.width - 400, this.height - 50)
          invalidInput = false
        }

        if (gotOutOfBalance) {
          g.setColor(Color.RED)
          g.drawString("Scale got out of balance!", this.width - 400, this.height - 50)
          gotOutOfBalance = false
        }

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
