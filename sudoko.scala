
sealed trait Box {
	def isOutOfBounds: Boolean
}

case object OutOfBounds extends Box {
	def isOutOfBounds = true
	override def toString: String = " * "
}

case object EmptyBox extends Box {
	def isOutOfBounds = false
	override def toString: String = """   """
}

case class FilledBox(value: Int) extends Box {
	def isOutOfBounds = false
	override def toString: String = s""" ${value} """
}

class Grid(val size: Int) {
	private var boxes = {
		(0 to size + 1).map { y =>
			(0 to size + 1).map { x =>
				val w = size + 1
				val h = size + 1
				(x,y) match {
					/* The perimeter */
					case (0, 0) | (0, `y`) | (`x`, 0) | (`w`, `h`) => OutOfBounds
					case (`w`, _) | (_, `h`) => OutOfBounds
					/* The corners */
					case _ => EmptyBox
				}
			}
		}
	}
	override def toString: String = "\n" + {
		boxes.map { row =>
			row.mkString("|") + "\n" + ("---+" * row.size).dropRight(1)
		}.mkString("\n")
	} + "\n"

	private def inGrid(int: Int) = {
		int > 0 && int < size + 1
	}

	def viewBoard = {
		println(this)
		this
	}

	def fillBoxAt(coordinate: (Int, Int), value: Int) = {
		val (x,y) = coordinate
		require(inGrid(x))
		require(inGrid(y))
		require(inGrid(value))

		boxes = {
			boxes.updated(
				y, 
				boxes(y).updated(x, FilledBox(value))
			)
		}
		this
	}

	def eraseBoxAt(coordinate: (Int, Int)) = {
		val (x,y) = coordinate
		require(inGrid(x))
		require(inGrid(y))

		boxes = {
			boxes.updated(
				y, 
				boxes(y).updated(x, EmptyBox)
			)
		}
		this
	}

	def isSolutionValid_? = {
		/* To be valid we need to have this row contains only 1 - size each one time, as well as the columns */
		var isValid = true
		(1 to size).foreach { y =>
			(1 to size).foreach { x =>
				boxes(y)(x) match {
					case EmptyBox | OutOfBounds => // No Op
					case FilledBox(value) => {
						/* Does this value appear above/below us? */
						(1  to y - 1) ++ (y + 1 to size) foreach { aboveOrBelow =>
							boxes(aboveOrBelow)(x) match {
								case FilledBox(otherValue) => 
									isValid = value != otherValue
								case EmptyBox | OutOfBounds=> // NoOp 
							}
						}

						/* Does this value appear to the left/right of us? */
						(1 to x - 1) ++ (x + 1 to size) foreach { leftOrRight =>
							boxes(y)(leftOrRight) match {
								case FilledBox(otherValue) => 
									isValid = value != otherValue
								case EmptyBox | OutOfBounds=> // NoOp
							}
						}
					}
				}
			}
		}
		isValid
	}	

}

object Game {
	def newGame() = {
		println("Creating new game...")
		val size = 6
		val g = new Grid(size)
		/* Randomly generate a grid filled with 2 values in each row */
		import scala.util.Random
		(1 to size).foreach { y =>
			var placed = Set.empty[Int]
			var values = Set.empty[Int]
			while(placed.size < 2) {
				val x = {
					var r = Random.nextInt(size) + 1
					while(placed.contains(r)) {
						r = Random.nextInt(size) + 1
					}
					r
				}
				val value = {
					var r = Random.nextInt(size) + 1
					while(values.contains(r)) {
						r = Random.nextInt(size) + 1
					}
					r
				}

				g.fillBoxAt((x,y), value)
				if (g.isSolutionValid_?) {
					placed += x
					values += value
				} else {
					g.eraseBoxAt((x,y))
				}
			}
		}
		println("Game ready! Have fun!")
		g
	}
}