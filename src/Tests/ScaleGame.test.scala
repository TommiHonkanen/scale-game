package Tests

import org.junit.Test
import org.junit.Assert._

import scala.collection.mutable.Map
import ScaleGame.{Player, Scale}

import java.awt.Color

// Tests the most important methods of the game
class UnitTests {

  // Holds the players
  val players1 = Array(new Player(Color.GREEN, 99), new Player(Color.RED, 99), new Player(Color.GREEN, 99), new Player(Color.BLUE, 99))


  // Scales used for the tests
  val A = new Scale(4, 'A')
  val B = new Scale(2, 'B')
  val C = new Scale(2, 'C')
  val D = new Scale(1, 'D')
  val E = new Scale(2, 'E')
  val F = new Scale(2, 'F')
  val G = new Scale(1, 'G')
  A.placeTiles()
  B.placeTiles()
  C.placeTiles()
  D.placeTiles()
  E.placeTiles()
  F.placeTiles()
  G.placeTiles()

  val a = players1(0)
  val b = players1(1)
  val c = players1(2)
  val d = players1(3)

  val players = Array(a, b, c, d)

  // Places the scales on top of each other
  A.rightTiles(2).scale = Some(B)
  A.leftTiles(3).scale = Some(C)
  B.rightTiles(1).scale = Some(D)
  C.leftTiles(1).scale = Some(E)
  D.leftTiles(0).scale = Some(F)
  E.rightTiles(1).scale = Some(G)

  // Places some weights on the scales
  G.placeWeight('R', 1, a)
  G.placeWeight('R', 1, a)
  G.placeWeight('L', 1, b)
  E.placeWeight('L', 1, c)
  C.placeWeight('R', 2, d)
  F.placeWeight('L', 2, d)
  F.placeWeight('R', 1, c)
  D.placeWeight('R', 1, b)
  B.placeWeight('L', 2, a)
  B.placeWeight('L', 1, d)
  A.placeWeight('L', 2, c)
  A.placeWeight('R', 4, d)

  // Tests the totalWeight method
  @Test def testTotalWeight(): Unit = {
    assertEquals(3, G.totalWeight())
    assertEquals(4, E.totalWeight())
    assertEquals(5, C.totalWeight())
    assertEquals(2, F.totalWeight())
    assertEquals(3, D.totalWeight())
    assertEquals(5, B.totalWeight())
    assertEquals(12, A.totalWeight())
  }

  // Tests the pointsPerPlayer method
  @Test def testPointsPerPlayer(): Unit = {
    assertEquals(G.pointsPerPlayer(players), Map(a -> 2, b -> 1, c -> 0, d -> 0))
    assertEquals(E.pointsPerPlayer(players), Map(a -> 4, b -> 2, c -> 1, d -> 0))
    assertEquals(C.pointsPerPlayer(players), Map(a -> 8, b -> 4, c -> 2, d -> 2))
    assertEquals(F.pointsPerPlayer(players), Map(a -> 0, b -> 0, c -> 1, d -> 2))
    assertEquals(D.pointsPerPlayer(players), Map(a -> 0, b -> 1, c -> 1, d -> 2))
    assertEquals(B.pointsPerPlayer(players), Map(a -> 2, b -> 2, c -> 2, d -> 5))
    assertEquals(A.pointsPerPlayer(players), Map(a -> 38, b -> 22, c -> 16, d -> 27))
  }

  // Tests the isBalanced method
  @Test def TestIsBalanced(): Unit = {
    assertTrue(G.isBalanced())
    assertTrue(!E.isBalanced())
    assertTrue(!C.isBalanced())
    assertTrue(F.isBalanced())
    assertTrue(D.isBalanced())
    assertTrue(!B.isBalanced())
    assertTrue(A.isBalanced())
  }
}


