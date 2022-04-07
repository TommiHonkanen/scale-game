package ScaleGame

/**
 * Each weight that is placed is a new weight object
 * the owner of a weight object can change if another player stacks a weight on top if it
 *
 * @param originalOwner the owner of this weight when it is first created
 */
class Weight(originalOwner: Player) {

  // Holds the owner of this weight
  var owner = originalOwner

}
