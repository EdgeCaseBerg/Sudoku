
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

case class UnEraseableBox(value: Int) extends Box {
	def isOutOfBounds = false
	override def toString: String = s""" ${value} """
}

class Game(val size: Int) {
	private var boxes = {
		(0 to size + 1).map { y =>
			(0 to size + 1).map { x =>
				val w = size + 1
				val h = size + 1
				(x,y) match {
					/* The perimeter */
					case (0, 0) | (0, `y`) | (`x`, 0) | (`w`, `h`) => OutOfBounds
					case (`w`, _) | (_, `h`) => OutOfBounds
					/* The center */
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

	def viewBoard {
		println(this)
	}

	private def placeUneraseableBoxAt(coordinate: (Int, Int), value: Int) = {
		val (x,y) = coordinate
		require(inGrid(x))
		require(inGrid(y))
		require(inGrid(value))

		boxes(y)(x) match {
			case ueb: UnEraseableBox => this
			case _ => {
				boxes = {
					boxes.updated(
						y, 
						boxes(y).updated(x, UnEraseableBox(value))
					)
				}
				this
			}
		}
	}

	private def eraseUneraseableBoxAt(coordinate: (Int, Int)) = {
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

	def fillBoxAt(coordinate: (Int, Int), value: Int) = {
		val (x,y) = coordinate
		require(inGrid(x))
		require(inGrid(y))
		require(inGrid(value))

		boxes(y)(x) match {
			case ueb: UnEraseableBox => // no op
			case _ => {
				boxes = {
					boxes.updated(
						y, 
						boxes(y).updated(x, FilledBox(value))
					)
				}
			}
		}
		isGameOver_?
		this
	}

	def eraseBoxAt(coordinate: (Int, Int)) = {
		val (x,y) = coordinate
		require(inGrid(x))
		require(inGrid(y))

		boxes(y)(x) match {
			case ueb: UnEraseableBox => this
			case _ => {
				boxes = {
					boxes.updated(
						y, 
						boxes(y).updated(x, EmptyBox)
					)
				}
				this
			}
		}
	}

	private def checkBox(x: Int, y: Int, value: Int, validOutSideThisRow: Boolean) = {
		var isValid = validOutSideThisRow
		/* Does this value appear above/below us? */
		(0  to (size + 1)).filterNot(_ == y).foreach { aboveOrBelow =>
			boxes(aboveOrBelow)(x) match {
				case FilledBox(otherValue) => 
					isValid = isValid && value != otherValue
				case UnEraseableBox(otherValue) => 
					isValid = isValid && value != otherValue
				case EmptyBox | OutOfBounds => // NoOp 
			}
		}

		/* Does this value appear to the left/right of us? */
		(0 to (size + 1)).filterNot(_ == x).foreach { leftOrRight =>
			boxes(y)(leftOrRight) match {
				case FilledBox(otherValue) => 
					isValid = isValid && value != otherValue
				case UnEraseableBox(otherValue) => 
					isValid = isValid && value != otherValue
				case EmptyBox | OutOfBounds=> // NoOp
			}
		}
		validOutSideThisRow && isValid
	}

	def isSolutionValid_? = {
		/* To be valid we need to have this row contains only 1 - size each one time, as well as the columns */
		var isValid = true
		(1 to size).foreach { y =>
			(1 to size).foreach { x =>
				boxes(y)(x) match {
					case EmptyBox | OutOfBounds => // No Op
					case FilledBox(value) => isValid = checkBox(x,y,value, isValid)
					case UnEraseableBox(value) => isValid = checkBox(x,y,value, isValid)
				}
			}
		}
		isValid
	}

	private def isGameOver_? = {
		var thereAreEmptyBoxes = false
		(1 to size).foreach { y =>
			(1 to size).foreach { x =>
				boxes(y)(x) match {
					case EmptyBox => thereAreEmptyBoxes = true
					case _ => // no op
				}
			}
		}
		if(!thereAreEmptyBoxes && isSolutionValid_?) {
			println("Congratulations! You've won!")
		}
	}

}

object Game {
	def newGame(size: Int = 6, initialValuesPerRow: Int = 2) = {
		require(initialValuesPerRow <= size)
		println("Creating new game...")
		val g = new Game(size)
		/* Randomly generate a grid filled with 'initialValuesPerRow' values in each row 
		 * Note that if you pass initialValuesPerRow = size you've just asked the system
		 * to try to generate a board that is completely solved at random. Which will 
		 * take a long time, not to mention it might not be possible anyway since we don't
		 * ever do any backtracking after we've move down a row. So yeah, basically just
		 * stick with 2 for the initial values, or maybe 3 if you've got a larger board than
		 * 6.
		 */
		import scala.util.Random
		(1 to size).foreach { y =>
			var placed = Set.empty[(Int, Int)]
			var values = Set.empty[Int]
			while(placed.size < initialValuesPerRow) {
				val coordinate = {
					var r = (Random.nextInt(size) + 1, y)
					while(placed.contains(r)) {
						r = (Random.nextInt(size) + 1, y)
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

				g.placeUneraseableBoxAt(coordinate, value)
				if (g.isSolutionValid_?) {
					placed += coordinate
					values += value
				} else {
					g.eraseUneraseableBoxAt(coordinate)
				}
			}
		}
		println("Game ready! Have fun!")
		g
	}
}