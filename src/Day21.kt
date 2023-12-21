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

        val countsAtDepth: MutableList<Long> = mutableListOf(onWhenEven)

        while (depth <= maxDepth) {
            measure {
                println("Depth: $depth")

                map[depth] = map[depth - 1]!!.flatMap { position: Position ->
                    Direction.entries.mapNotNull { direction ->
                        val newPosition = position.travel(direction)
                        if (field.isNotARockOnInfiniteField(newPosition)) {
                            if (map.values.any { it.contains(newPosition) }) {
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
                    countsAtDepth.add(onWhenEven)
                } else {
                    onWhenOdd += map[depth]!!.size
                    countsAtDepth.add(onWhenOdd)
                }

                if (depth >= 2) {
                    map.remove(depth - 2)
                }
                depth += 1
            }
        }

        countsAtDepth.forEachIndexed { index: Int, count: Long ->

            val diffToPreviousMinusX = if (index > 262) {
                count - countsAtDepth[index - 262]
            } else { 0 }


            if (index - 524 - 65 >= 0 && (index - 524 - 65) % 262 == 0) {
                println("Depth $index: count to index-262: $diffToPreviousMinusX (total $count)")
            }
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

    testFile(
        "Part 2 Test 6 Steps", "Day21_test",
        { part2(it, steps = 6) }, 16,
    )
    testFile(
        "Part 2 Test 10 Steps", "Day21_test",
        { part2(it, steps = 10) }, 50,
    )
    testFile(
        "Part 2 Test 50 Steps", "Day21_test",
        { part2(it, steps = 50) }, 1594,
    )
    testFile(
        "Part 2 Test 100 Steps", "Day21_test",
        { part2(it, steps = 100) }, 6536,
    )
    testFile(
        "Part 2 Test 500 Steps", "Day21_test",
        { part2(it, steps = 500) }, 167004,
    )
    testFile(
        "Part 2 Test 1000 Steps", "Day21_test",
        { part2(it, steps = 1000) }, 668697,
    )
    testFile(
        "Part 2 Test 5000 Steps", "Day21_test",
        { part2(it, steps = 5000) }, 16733044,
    )

    val input2 = readInput("Day21").filter(String::isNotBlank)
    measure { part2(input2, steps = 2000) }

    /** For part 2, cycle length is always (biggest dimension * 2)
     * Let the cycle length be c, let the index of the first cycle be c_0
     * let on(x) denote the fields that the gardener can be on at timestamp x
     *
     * A cycle satisfies
     *   on[c_0 + k * c] - on[c_0 + (k-1) * c] stays constant
     *     where k is index
     *   => the delta of the increase is constant if you look at elements with the same index in the cycle
     *   For example: cycle length 22, consider the cycle as starting at 65
     *     => 65, 87, 109
     *     65  has a total value  2722 and a delta to  43 of 1576,
     *     87  has a total value  4946 and a delta to  65 of 2224, the delta increased by 648
     *     109 has a total value  7818 and a delta to  89 of 2872, the delta increased by 648
     *     131 has a total value 11338 and a delta to 109 of 3520, the delta increased by 648
     * Modify this so that it's
     * This can be written as a recursive function:
     *   on_(n) = on_(n-1) + 1576 + 648*n
     *   on_(0) = 2722
     *   where n is: 0 for depth 65, 1 for depth 87 (so same element after first cycle), and so on
     *   solving for the recurrence relation (Wolfram Alpha) on_(n) = 4n * (81n + 475) + 2722
     */

    /**
     * Given the above, we can "manually" find
     *   the cycle length: 262
     *   => we want to find value at 26501365, which is the 65th element in the cycle
     *   the index where the first cycle starts: 524
     *   => index to consider is 524+65 = 589
     *
     *   index to n
     *   on(589) = on_(0)
     *   on(589 + 262) = on_(1)
     */
    fun indexToN(index: Int) =
        (index - 524 - 65) / 262

    /**
     * We also know `on_(0) = 310 036`
     * The initial delta: 214 220
     * And the delta increase each cycle: 122 288
     * => on_(n) = on_(n-1) + 214 220 + 122 288 * n
     * => solve for recurrence relation: on_(n) = 4 * (n * (15286*n + 68841) + 77509)
     * => solve for recurrence relation: on_(n) = 61144*n*n + 275_364*n + 310_036
     */
    fun x(n: Int) =
        61144L*n*n + 275_364L*n + 310_036L

    // Now, we get the index of the step we want to get
    val n = indexToN(26501365)
    // And then we calculate the amount of steps :D
    x(n)
        .also { check(it == 625587097150084L) }
        .println()
}
