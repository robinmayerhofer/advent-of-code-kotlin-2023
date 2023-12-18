
typealias DigitField = Array<IntArray>

fun inputToDigitField(input: List<String>): DigitField {
    val rows = input.size
    val columns = input.firstOrNull()?.length ?: 0

    val field = Array(rows) { IntArray(columns) }

    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, character ->
            field[row][column] = character.digitToInt()
        }
    }
    return field
}

operator fun DigitField.get(position: Position) =
    this[position.row][position.column]

fun DigitField.get(row: Int, column: Int): Int =
    this[row][column]

operator fun DigitField.set(position: Position, newValue: Int) {
    this[position.row][position.column] = newValue
}

fun DigitField.set(row: Int, column: Int, newValue: Int) {
    this[row][column] = newValue
}

fun DigitField.isValidPosition(position: Position): Boolean =
    if (position.column < 0 || position.row < 0) {
        false
    } else if (position.column >= this[0].size || position.row >= this.size) {
        false
    } else {
        true
    }

fun DigitField.println() {
    println(this.joinToString("\n") { it.joinToString("") })
}