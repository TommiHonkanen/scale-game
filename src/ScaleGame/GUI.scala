package ScaleGame

import scala.collection.mutable.Buffer
import scala.swing.BorderPanel.Position._
import scala.swing.event.{ButtonClicked, SelectionChanged, ValueChanged}
import java.awt.{BasicStroke, Color, Font}
import scala.swing.{BorderPanel, Button, ComboBox, Dimension, Frame, Graphics2D, GridPanel, Label, ListView, MainFrame, Orientation, Panel, Rectangle, SimpleSwingApplication, Slider, SplitPane}

object GUI extends SimpleSwingApplication {

  val players = Array(new Player(Color.GREEN, 20), new Player(Color.RED, 20), new Player(Color.ORANGE, 20), new Player(Color.BLUE, 20))

  var game: Game = new Game(players, 20)

  val firstScale = new Scale(game.random.nextInt(5) + 6, 'A')

  firstScale.placeTiles()

  game.scales += firstScale

  var turn = players.head

  /*

  val newScaleProbability1 = 50

  val game = new Game(players, newScaleProbability1)

  val testScale = new Scale(5, 'A')

  val testScale2 = new Scale(3, 'B')

  val testScale3 = new Scale(1, 'C')

  testScale.placeTiles()

  testScale2.placeTiles()

  testScale3.placeTiles()

  game.scales += testScale

  game.scales += testScale2

  game.scales += testScale3

  testScale.leftTiles(2).scale = Option(testScale2)

  testScale2.rightTiles(1).scale = Option(testScale3)

  testScale3.placeWeight('R', 1, players(0))

  var gameStarted = false

   */

  var gameStarted = false

  def top = new MainFrame {
    title = "Scale Game"

    val freeScales = game.scales.filter(scale => scale.leftTiles.exists(_.scale.isEmpty) || scale.rightTiles.exists(_.scale.isEmpty)).map(_.symbol)

    var currentScale = game.scales.find(_.symbol == freeScales.head).get

    val scaleList = new ComboBox(freeScales) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => {

          currentScale = game.scales.find(_.symbol == selection.item).get
          distance.max = currentScale.radius

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
      max = 100
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

    val startGame = Button("Start Game") {
      // gameStarted = true

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

      players.foreach(_.weightsLeft = currentWeightAmount)

      game = new Game(players.take(currentPlayerAmount), currentNewScaleProbability)

      game.scales += firstScale
    }

    val submitButton = Button("Submit") {
      println(currentScale.symbol)
      println(currentSide)
      println(currentDistance)

      val testScale4 = new Scale(6, 'D')

      testScale4.placeTiles()

      //testScale3.leftTiles(0).scale = Option(testScale4)

      ScalePanel.visible = false

      ScalePanel.repaint

      ScalePanel.visible = true

      println(game.newScaleProbability)
      println(game.players.length)
      println(game.players.head.weightsLeft)
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
              paintScale(tile.scale.get, rect.x, rect.y - this.squareWidth, math.max(scale.weightHeight() + 1, 2))
            } else if (tile.weights.nonEmpty) {
              for (i <- 1 to tile.weights.length) {
                g.setColor(tile.weights.head.owner.color)
                g.fillOval(rect.x, rect.y - i * rect.y, this.squareWidth, this.squareWidth)
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
              paintScale(tile.scale.get, rect.x, rect.y - this.squareWidth, math.max(scale.weightHeight() + 1, 2))
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
        g.drawString("Turn: Player " + (game.players.indexOf(turn) + 1) + "(" + turn + ")", this.width / 2 - 90, this.height - 50)

        for (player <- points) {
          g.setColor(player._1.color)
          g.drawString(player._1 + ": " + player._2, 50, points.indexOf(player) * 20 + this.height - 150)
          g.drawString(player._1 + ": " + player._1.weightsLeft, 225, points.indexOf(player) * 20 + this.height - 150)
        }


      }
      preferredSize = new Dimension(this.width, this.height)
    }



    if (!gameStarted) {
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
    }





    // size = new Dimension(1440, 1000)
    centerOnScreen()

  }
}
