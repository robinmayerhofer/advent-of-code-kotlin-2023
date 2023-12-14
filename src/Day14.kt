
fun main() {

    val ROUNDED_ROCK = 'O'
    val CUBE_SHAPED_ROCK = '#'
    val EMPTY_SPACE = '.'

    fun Field.tiltNorth() {
        val xs = this[0].indices
        val ys = this.indices

        xs.forEach { x ->
            var nextShiftLocation: Int? = null
            for (y in ys) {
                when (this[y][x]) {
                    ROUNDED_ROCK -> {
                        if (nextShiftLocation != null) {
                            this[y][x] = EMPTY_SPACE
                            this[nextShiftLocation][x] = ROUNDED_ROCK
                            nextShiftLocation += 1
                        }
                    }
                    CUBE_SHAPED_ROCK -> nextShiftLocation = null
                    EMPTY_SPACE -> {
                        if (nextShiftLocation == null) {
                            nextShiftLocation = y
                        }
                    }
                }
            }
        }
    }

    fun Field.calculateLoad(): Long {
        val lines = size.toLong()

        return mapIndexed { index, chars ->
            val multiplier = lines - index
            chars.count { it == ROUNDED_ROCK } * multiplier
        }.sum()
    }

    fun part1(input: List<String>): Long {
        val field = inputToField(input)
        field.tiltNorth()
        return field.calculateLoad()
    }

    fun part2(input: List<String>): Long =
        TODO()

    testFile(
        "Part 1 Test 1",
        "Day14_test",
        ::part1,
        136L
    )

    val input = readInput("Day14").filter(String::isNotBlank)
    part1(input).println()

    testFile(
        "Part 2 Test 1",
        "Day14_test",
        ::part2,
        1
    )
    val input2 = readInput("Day14_2").filter(String::isNotBlank)
    part2(input2).println()
}
