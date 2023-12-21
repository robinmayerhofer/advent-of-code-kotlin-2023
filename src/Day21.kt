private const val START = 'S'
private const val GARDEN = '.'
private const val ROCK = '#'

private typealias Depth = Int

fun main() {
    shouldLog = true

    fun Field.isValidAndNotARock(position: Position): Boolean =
        isValidPosition(position) && this[position] != ROCK

    fun breadthFirst(
        field: Field,
        startPosition: Position,
        maxDepth: Int
    ): Int {
        val map: MutableMap<Depth, Set<Position>> = mutableMapOf(
            0 to setOf(startPosition)
        )

        var depth = 1
        while (depth <= maxDepth) {
            map[depth] = map[depth - 1]!!.flatMap { position: Position ->
                Direction.entries.mapNotNull { direction ->
                    val newPosition = position.travel(direction)
                    if (field.isValidAndNotARock(newPosition)) {
                        newPosition
                    } else {
                        null
                    }
                }
            }.toSet()
            if (depth >= 1) {
                map.remove(depth - 1)
            }
            depth += 1
        }

        return map[maxDepth]!!.size
    }

    fun Field.elementFromInfinitelyRepeatingField(position: Position): Char {
        val columns = this[0].size
        val rows = this[0].size
        return this[Position(
            column = (position.column % columns + columns) % columns,
            row = (position.row % rows + rows) % rows,
        )]
    }

    fun Field.isNotARockOnInfiniteField(position: Position): Boolean =
        elementFromInfinitelyRepeatingField(position) != ROCK

    fun breadthFirstInfinite(
        field: Field,
        startPosition: Position,
        maxDepth: Long
    ): Long {
        val map: MutableMap<Depth, Set<Position>> = mutableMapOf(
            0 to setOf(startPosition)
        )

        var depth = 1

        var onWhenEven = 1L
        var onWhenOdd = 0L

        while (depth <= maxDepth) {
            println("Depth: $depth")
            val recentlyVisitedPositions: Set<Position> = map.values.flatten().toSet()

            map[depth] = map[depth - 1]!!.flatMap { position: Position ->
                Direction.entries.mapNotNull { direction ->
                    val newPosition = position.travel(direction)
                    if (field.isNotARockOnInfiniteField(newPosition)) {
                        if (recentlyVisitedPositions.contains(newPosition)) {
                            null
                        } else {
                            newPosition
                        }
                    } else {
                        null
                    }
                }
            }.toSet()

            if (depth % 2 == 0) {
                onWhenEven += map[depth]!!.size
            } else {
                onWhenOdd += map[depth]!!.size
            }

//            println("$depth => ${map[depth]!!.size}")
//            println("$depth => Diff: ${map[depth]!!.size - (map[depth-1]?.size ?: 0)}")

            if (depth >= 10) {
                map.remove(depth - 10)
            }
            depth += 1
        }

        return if (maxDepth % 2 == 0L) {
            onWhenEven
        } else {
            onWhenOdd
        }
    }

    fun part1(input: List<String>, steps: Int): Int {
        val field = inputToField(input)
        val startPosition = field.find { it == START }
        field[startPosition] = GARDEN
        return breadthFirst(field, startPosition, steps)
    }

    fun part2(input: List<String>, steps: Long): Long {
        val field = inputToField(input)
        val startPosition = field.find { it == START }
        field[startPosition] = GARDEN
        return breadthFirstInfinite(field, startPosition, steps)
    }


    testFile(
        "Part 1 Test 1",
        "Day21_test",
        { part1(it, steps = 6) },
        16,
    )

    val input = readInput("Day21").filter(String::isNotBlank)
    part1(input, steps = 64).println()

//    testFile(
//        "Part 2 Test 6 Steps", "Day21_test",
//        { part2(it, steps = 6) }, 16,
//    )
//    testFile(
//        "Part 2 Test 10 Steps", "Day21_test",
//        { part2(it, steps = 10) }, 50,
//    )
//    testFile(
//        "Part 2 Test 50 Steps", "Day21_test",
//        { part2(it, steps = 50) }, 1594,
//    )
//    testFile(
//        "Part 2 Test 100 Steps", "Day21_test",
//        { part2(it, steps = 100) }, 6536,
//    )
//    testFile(
//        "Part 2 Test 500 Steps", "Day21_test",
//        { part2(it, steps = 500) }, 167004,
//    )
//    testFile(
//        "Part 2 Test 1000 Steps", "Day21_test",
//        { part2(it, steps = 1000) }, 668697,
//    )
    testFile(
        "Part 2 Test 5000 Steps", "Day21_test",
        { part2(it, steps = 5000) }, 16733044,
    )
//    val input2 = readInput("Day21").filter(String::isNotBlank)
//    part2(input2).println()
}
