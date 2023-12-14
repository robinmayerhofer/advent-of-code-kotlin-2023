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

    fun Field.copy() = Array(size) { y ->
        CharArray(size) { x ->
            this[y][x]
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

    fun fieldsEqual(f1: Field, f2: Field): Boolean =
        f1.zip(f2).all { (r1, r2) ->
            r1.contentEquals(r2)
        }

    fun part2(input: List<String>): Long {
        var field = inputToField(input)

        val indexToField = mutableListOf<Field>()

        // for Part 2 real input:
        // from 107th position on we have a cycle with the length of 11
        // (1000000000 - 107) / 11 = 90.909.081,1818181818
        // 1000000000 - 90.909.081 * 11 - 107 = 2
        // => TRY 109th value

        (0 until 107 + 2).forEach { i ->
            indexToField
                .mapIndexedNotNull { index, value ->
                    if (fieldsEqual(value, field)) {
                        index
                    } else {
                        null
                    }
                }
                .takeIf { it.isNotEmpty() }
                ?.let { sameIndices ->
                    val load = field.calculateLoad()
                    println(
                        "Index $i has same field (load $load) as indices (last 10): ${
                            sameIndices.takeLast(10).reversed()
                        }"
                    )
                }
            indexToField.add(field.copy())

            field.tiltNorth() // NORTH
            field = rotate90CounterClockwise(field)
            field.tiltNorth() // WEST
            field = rotate90CounterClockwise(field)
            field.tiltNorth() // SOUTH
            field = rotate90CounterClockwise(field)
            field.tiltNorth() // EAST
            field = rotate90CounterClockwise(field)
        }

        return field.calculateLoad()
    }

    testFile(
        "Part 1 Test 1",
        "Day14_test",
        ::part1,
        136L
    )

    val input = readInput("Day14").filter(String::isNotBlank)
    part1(input).println()

//    testFile(
//        "Part 2 Test 1",
//        "Day14_test",
//        ::part2,
//        64
//    )
    val input2 = readInput("Day14").filter(String::isNotBlank)
    part2(input2)
        .also { check(it < 99944) }
        .also { check(it > 96097) }
        .println()
}
