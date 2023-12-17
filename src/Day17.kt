import java.util.*

private enum class Dir(val deltaX: Int, val deltaY: Int) {
    NORTH(-1, 0),
    EAST(0, -1),
    SOUTH(1, 0),
    WEST(0, 1),
    ;

    fun reverse() = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
    }
}

fun main() {

    data class LastDirection(
        val dir: Dir,
        val amount: Int
    )

    data class CacheValue(
        val position: Position,
        val costUntilPreviousPosition: Int,
        val lastDirection: LastDirection?,
    ) : Comparable<CacheValue> {
        override fun compareTo(other: CacheValue): Int =
            costUntilPreviousPosition - other.costUntilPreviousPosition
    }

    fun Position.travel(direction: Dir): Position =
        copy(column = column + direction.deltaX, row = row + direction.deltaY)

    fun aStarThisField(field: DigitField): Int {
        val priorityQueue = PriorityQueue<CacheValue>()
        val cache: MutableMap<Pair<Position, Dir>, CacheValue> = mutableMapOf()

        val startPosition = Position(
            row = 0,
            column = 0,
        )
        val endPosition = Position(
            row = field.size - 1,
            column = field[0].size - 1,
        )

        priorityQueue.add(
            CacheValue(
                position = Position(0, 0),
                costUntilPreviousPosition = -field[0][0],
                lastDirection = null,
            )
        )
        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()
            if (!field.isValidPosition(current.position)) {
                println("Skipping invalid position: $current")
                continue
            }
            if (current.position == startPosition && current.lastDirection != null) {
                println("Skipping start position after circle: $current")
                continue
            }

            val costOfPosition = field.elementAt(current.position)
            // End condition
            if (current.position == endPosition) {
                println("Found end position: $current")
                return current.costUntilPreviousPosition + costOfPosition
            }

            if (current.lastDirection != null) {
                val cachedValue: CacheValue? = cache[current.position to current.lastDirection.dir]
                if (cachedValue != null) {
                    if (cachedValue.lastDirection != null) {
                        if (cachedValue.lastDirection.amount <= current.lastDirection.amount) {
                            if (cachedValue.costUntilPreviousPosition <= current.costUntilPreviousPosition) {
                                println("Found better entry in cache: $current, better entry: $cachedValue")
                                continue
                            }
                        }
                    }
                }

                cache[current.position to current.lastDirection.dir] = current
            }

            println("Trying: $current")

            val forbiddenDirections =
                current.lastDirection?.dir?.reverse()?.let { mutableListOf(it) } ?: mutableListOf()
            if (current.lastDirection?.amount == 3) {
                forbiddenDirections.add(current.lastDirection.dir)
            } // should not go forward

            for (direction in Dir.entries) {
                if (direction in forbiddenDirections) {
                    continue
                }

                val newLastDirection = LastDirection(
                    dir = direction,
                    amount = if (direction == current.lastDirection?.dir) {
                        current.lastDirection.amount + 1
                    } else {
                        1
                    }
                )

                val next = CacheValue(
                    position = current.position.travel(direction),
                    costUntilPreviousPosition = current.costUntilPreviousPosition + costOfPosition,
                    lastDirection = newLastDirection
                )
                println("Adding: $next")
                priorityQueue.add(next)
            }

            println()
        }

        error("Oops, found no end")
    }


    fun part1(input: List<String>): Int {
        val field = inputToDigitField(input)
        return aStarThisField(field)
    }

    fun part2(input: List<String>): Int {
        val field = inputToField(input).map {
            it.map { it.digitToInt() }
        }

        return 0
    }

    test(
        "Part 1 Test 1",
        """
            011
            111
            111
        """.trimIndent(),
        ::part1,
        4,
    )

    testFile(
        "Part 1 Test 1",
        "Day17_test",
        ::part1,
        102,
    )

    println("Passed tests for Part 1")
    val input = readInput("Day17").filter(String::isNotBlank)
    part1(input).println()

//    testFile(
//            "Part 2 Test 1",
//            "Day17_test",
//            ::part2,
//            1
//    )
//    val input2 = readInput("Day17_2").filter(String::isNotBlank)
//    part2(input2).println()
}
