fun main() {

    data class NumberOnField(
            val row: Int,
            val columnRange: IntRange,
            val number: Int
    )

    data class PotentialGearOnField(
            val row: Int,
            val column: Int
    )

    data class Gear(
            val row: Int,
            val column: Int,
            val adjacentNumbers: Pair<NumberOnField, NumberOnField>,
    ) {
        fun gearRatio() = adjacentNumbers.first.number * adjacentNumbers.second.number
    }

    fun inputToNumbersOnField(input: List<String>): List<NumberOnField> {
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

    fun inputToPotentialGearsOnField(input: List<String>): List<PotentialGearOnField> {
        val regex = "(\\*)".toRegex()
        return input.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { index, char ->
                if (char == '*') {
                    PotentialGearOnField(row = row, column = index)
                } else {
                    null
                }
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
        for (r in row - 1..row + 1) {
            for (c in columnRange.first - 1..columnRange.last + 1) {
                if (field.hasSymbolAt(r, c)) {
                    return true
                }
            }
        }

        return false
    }

    fun part1(input: List<String>): Int {
        val field = inputToField(input)
        val numbers = inputToNumbersOnField(input)

        return numbers
                .filter { it.isPartNumber(field) }
                .sumOf(NumberOnField::number)
    }

    fun NumberOnField.isAdjacentTo(potentialGear: PotentialGearOnField): Boolean {
        if (row - 1 > potentialGear.row || row + 1 < potentialGear.row) {
            return false
        }

        if (potentialGear.column > columnRange.last + 1 || potentialGear.column < columnRange.first - 1) {
            return false
        }

        return true
    }

    fun part2(input: List<String>): Int {
        val numbers = inputToNumbersOnField(input)
        val potentialGears = inputToPotentialGearsOnField(input)

        val gears: Collection<Gear> = potentialGears
                .mapNotNull { potentialGear ->
                    numbers
                            .filter { it.isAdjacentTo(potentialGear) }
                            .takeIf { it.size == 2 }
                            ?.let { adjacentNumbers ->
                                Gear(
                                        row = potentialGear.row,
                                        column = potentialGear.column,
                                        adjacentNumbers = Pair(adjacentNumbers[0], adjacentNumbers[1])
                                )
                            }
                }

        return gears.sumOf { it.gearRatio() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()

    check(part2(testInput) == 467835)
    part2(input).println()
}
