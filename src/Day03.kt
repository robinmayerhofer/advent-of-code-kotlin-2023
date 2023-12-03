import kotlin.properties.Delegates

typealias Field = Array<CharArray>

fun main() {

    fun inputToField(input: List<String>): Array<CharArray> {
        val rows = input.size
        val columns = input.firstOrNull()?.length ?: 0

        val field = Array(rows) { CharArray(columns) }

        input.forEachIndexed { row, line ->
            line.forEachIndexed { column, character ->
                field[row][column] = character
            }
        }
        return field
    }

    data class NumberOnField(
            val row: Int,
            val columnRange: IntRange,
            val number: Int
    )

    fun inputToNumberCoordinates(input: List<String>): List<NumberOnField> {
        val regex = "(\\d+)".toRegex()
        return input.flatMapIndexed { row, line ->
            regex.findAll(line).map { result ->
                val group = result.groups[0]!!
                NumberOnField(
                        columnRange = group.range,
                        row = row,
                        number = group.value.toInt(),
                )
            }
        }
    }

    fun Char.isSymbol() =
            !(this.isDigit() || this == '.')

    fun Field.hasSymbolAt(row: Int, column: Int): Boolean =
        if (row < 0 || row >= size) {
            false
        } else if (column < 0 || column >= this[row].size) {
            false
        } else {
            this[row][column].isSymbol()
        }


    fun NumberOnField.isPartNumber(field: Field): Boolean {
        var rowToCheck by Delegates.notNull<Int>()
        var columnToCheck by Delegates.notNull<Int>()

        // check row above
        rowToCheck = row - 1
        if (row >= 0) {
            for (columnToCheck in (columnRange.first-1..columnRange.last+1)) {
                if (field.hasSymbolAt(rowToCheck, columnToCheck)) {
                    return true
                }
            }
        }

        // check middle row
        rowToCheck = row

        columnToCheck = columnRange.first - 1
        if (field.hasSymbolAt(rowToCheck, columnToCheck)) {
            return true
        }
        columnToCheck = columnRange.last + 1
        if (field.hasSymbolAt(rowToCheck, columnToCheck)) {
            return true
        }

        // check row below
        rowToCheck = row + 1
        if (row >= 0) {
            for (columnToCheck in (columnRange.first-1..columnRange.last+1)) {
                if (field.hasSymbolAt(rowToCheck, columnToCheck)) {
                    return true
                }
            }
        }

        println("Is not a part number: $this")
        return false
    }

    fun part1(input: List<String>): Int {
        val field = inputToField(input)
        val numbers = inputToNumberCoordinates(input)

        return numbers
                .filter { it.isPartNumber(field) }
                .sumOf(NumberOnField::number)
    }

    fun part2(input: List<String>): Int =
            input.sumOf {
                it.length
            }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()

//    val testInput2 = readInput("Day03_test02")
//    check(part2(testInput2) == 1)
//    part2(input).println()
}
