package ScaleGame

import java.awt.geom.Ellipse2D
import scala.collection.mutable.Buffer
import scala.swing.BorderPanel.Position._
import scala.swing.event.{ButtonClicked, SelectionChanged}
import java.awt.{BasicStroke, Color}
import scala.swing.{BorderPanel, Button, ComboBox, Dimension, Frame, Graphics2D, GridPanel, ListView, MainFrame, Orientation, Panel, Rectangle, SimpleSwingApplication, SplitPane}

object GUI extends SimpleSwingApplication {


  val players = Array(new Player('a', 20), new Player('b', 20))

  val newScaleProbability = 50

  val game = new Game(players, newScaleProbability)

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


  def top = new MainFrame {
    title = "Scale Game"

    val freeScales = game.scales.filter(scale => scale.leftTiles.exists(_.scale.isEmpty) || scale.rightTiles.exists(_.scale.isEmpty)).map(_.symbol)

    var currentScale = game.scales.find(_.symbol == freeScales.head).get

    val scaleList = new ComboBox(freeScales) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => { currentScale = game.scales.find(_.symbol == selection.item).get }
      }
    }

    def freeSides() = {
      val sides: Buffer[Char] = Buffer()

      if (currentScale.leftTiles.exists(_.scale.isEmpty)) sides += 'L'
      if (currentScale.rightTiles.exists(_.scale.isEmpty)) sides += 'R'

      sides
    }

    var currentSide = freeSides().head

    val side = new ComboBox(freeSides()) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => { currentSide = selection.item }
      }
    }

    def freeTiles() = {
      if (currentSide == 'L') currentScale.leftTiles.filter(_.scale.isEmpty).map(_.distance)
      else currentScale.rightTiles.filter(_.scale.isEmpty).map(_.distance)
    }

    var currentDistance = freeTiles().head

    val distance = new ComboBox(freeTiles()) {
      listenTo(selection)

      reactions += {
        case e:SelectionChanged => { currentDistance = selection.item }
      }
    }

    val button = new Button("Submit") {

    }


    class ScalePanel extends Panel {

      val width = 1440
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
                g.setColor(Color.GREEN)
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
                g.setColor(Color.GREEN)
                g.fillOval(rect.x, rect.y - i *  this.squareWidth, this.squareWidth, this.squareWidth)
              }
            }
          }

        }
        /*
        g.setColor(Color.GREEN)
        g.fillRect(0, height + squareWidth, 99999, squareWidth)
        g.setColor(Color.BLACK)
        g.fill(new Rectangle(this.width / 2 - this.squareWidth / 2, this.height, this.squareWidth, this.squareWidth))
        g.fillRect(this.width / 2 - this.squareWidth / 2, this.height - squareWidth, this.squareWidth, this.squareWidth)
        g.fillRect(this.width / 2 - this.squareWidth / 2, this.height - squareWidth, this.squareWidth, this.squareWidth)
        */

        paintScale(testScale, this.width / 2, this.height / 2, 3)
      }
      preferredSize = new Dimension(this.width, this.height)
    }

    contents = new BorderPanel {
      layout += new GridPanel(1, 4) {
        contents += scaleList
        contents += side
        contents += distance
        contents += button

      } -> North
      layout += new ScalePanel -> Center
    }

    size = new Dimension(1440, 1000)
    centerOnScreen()

  }
}
