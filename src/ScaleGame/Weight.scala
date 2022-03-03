package ScaleGame

class Weight(originalOwner: Player) {

  var owner = originalOwner

  override def toString = s"W: ${this.owner}"

}
