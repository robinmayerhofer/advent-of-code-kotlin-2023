import java.util.*

private enum class Dir(val deltaX: Int, val deltaY: Int) {
    NORTH(deltaX = 0, deltaY = -1),
    SOUTH(deltaX = 0, deltaY = 1),
    EAST(deltaX = -1, deltaY = 0),
    WEST(deltaX = 1, deltaY = 0),
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
        val previous: CacheValue?,
        val costUntilPreviousPosition: Int,
        val lastDirection: LastDirection?,
    ) : Comparable<CacheValue> {
        override fun compareTo(other: CacheValue): Int =
            costUntilPreviousPosition - other.costUntilPreviousPosition
    }

    fun Position.travel(direction: Dir): Position =
        copy(column = column + direction.deltaX, row = row + direction.deltaY)

//    fun aStarThisFieldP1(field: DigitField): Int {
//        val priorityQueue = PriorityQueue<CacheValue>()
//        val cache: MutableMap<Pair<Position, Dir>, CacheValue> = mutableMapOf()
//
//        val startPosition = Position(
//            row = 0,
//            column = 0,
//        )
//        val endPosition = Position(
//            row = field.size - 1,
//            column = field[0].size - 1,
//        )
//
//        priorityQueue.add(
//            CacheValue(
//                position = Position(0, 0),
//                costUntilPreviousPosition = -field[0][0],
//                lastDirection = null,
//            )
//        )
//        while (priorityQueue.isNotEmpty()) {
//            val current = priorityQueue.poll()
//            println("Trying: $current")
//
//            if (!field.isValidPosition(current.position)) {
//                println("Skipping invalid.")
//                continue
//            }
//            if (current.position == startPosition && current.lastDirection != null) {
//                println("Skipping start position after circle.")
//                continue
//            }
//
//            val costOfPosition = field.elementAt(current.position)
//            // End condition
//            if (current.position == endPosition) {
//                println("Found end position.")
//                return current.costUntilPreviousPosition + costOfPosition
//            }
//
//            if (current.lastDirection != null) {
//                val cachedValue: CacheValue? = cache[current.position to current.lastDirection.dir]
//                if (cachedValue != null) {
//                    if (cachedValue.lastDirection != null) {
//                        if (cachedValue.lastDirection.amount <= current.lastDirection.amount) {
//                            if (cachedValue.costUntilPreviousPosition <= current.costUntilPreviousPosition) {
//                                println("Found better entry in cache: $cachedValue")
//                                continue
//                            }
//                        }
//                    }
//                }
//
//                cache[current.position to current.lastDirection.dir] = current
//            }
//
//            val forbiddenDirections =
//                current.lastDirection?.dir?.reverse()?.let { mutableListOf(it) } ?: mutableListOf()
//            if (current.lastDirection?.amount == 3) {
//                forbiddenDirections.add(current.lastDirection.dir)
//            } // should not go forward
//
//            for (direction in Dir.entries) {
//                if (direction in forbiddenDirections) {
//                    continue
//                }
//
//                val newLastDirection = LastDirection(
//                    dir = direction,
//                    amount = if (direction == current.lastDirection?.dir) {
//                        current.lastDirection.amount + 1
//                    } else {
//                        1
//                    }
//                )
//
//                val next = CacheValue(
//                    position = current.position.travel(direction),
//                    costUntilPreviousPosition = current.costUntilPreviousPosition + costOfPosition,
//                    lastDirection = newLastDirection
//                )
//                println("Adding: $next")
//                priorityQueue.add(next)
//            }
//
//            println()
//        }
//
//        error("Oops, found no end")
//    }

    fun CacheValue.printPath() {
        val positions = mutableListOf<Position>()

        var current: CacheValue? = this
        while (current != null) {
            positions.add(current.position)
            current = current.previous
        }
        positions.reversed().println()
    }

    fun aStarThisFieldP2(field: DigitField): Int {
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
                previous = null
            )
        )

        var steps = 0
        var lastQueueSize = 0

        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()
//            println("Trying: $current")
            if (!field.isValidPosition(current.position)) {
//                println("Skipping invalid position.")
                continue
            }
            if (current.position == startPosition && current.lastDirection != null) {
//                println("Skipping start position after circle.")
                continue
            }

            val costOfPosition = field.elementAt(current.position)
            // End condition
            if (current.position == endPosition && (current.lastDirection?.amount ?: 0) >= 4) {
//                println("Found end position.")
                current.printPath()
                return current.costUntilPreviousPosition + costOfPosition
            }

            if (current.lastDirection != null) {
                val cachedValue: CacheValue? = cache[current.position to current.lastDirection.dir]
                if (cachedValue != null) {
                    if (cachedValue.lastDirection != null) {
                        if (
                            cachedValue.lastDirection.amount == current.lastDirection.amount ||
                            (cachedValue.lastDirection.amount >= 4 && cachedValue.lastDirection.amount <= current.lastDirection.amount)
                        ) {
                            if (cachedValue.costUntilPreviousPosition <= current.costUntilPreviousPosition) {
//                                println("Found better entry in cache: $cachedValue.")
                                continue
                            }
                        }
                    }
                }

                if (current.lastDirection.amount >= 4) {
                    cache[current.position to current.lastDirection.dir] = current
                }
            }


            val allowedDirections = mutableSetOf<Dir>()
            if (current.lastDirection != null) {
                if (current.lastDirection.amount < 10) {
                    allowedDirections.add(current.lastDirection.dir)
                }

                if (current.lastDirection.amount >= 4) {
                    allowedDirections.addAll(
                        Dir.entries
                            .toList()
                            .filter { it != current.lastDirection.dir && it != current.lastDirection.dir.reverse() }
                    )
                }
            } else {
                allowedDirections.addAll(Dir.entries.toList())
            }

//            println("Allowed directions: $allowedDirections")

            for (direction in allowedDirections) {
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
                    lastDirection = newLastDirection,
                    previous = current,
                )
                if (field.isValidPosition(next.position)) {
//                    println("Adding: $next")
                    priorityQueue.add(next)
                }
            }

            steps += 1
            if (steps % 1_000_000 == 0) {
                println("Queue Size: ${priorityQueue.size} (shrank? ${priorityQueue.size < lastQueueSize})")
                lastQueueSize = priorityQueue.size
            }
        }

        error("Oops, found no end")
    }


//    fun part1(input: List<String>): Int {
//        val field = inputToDigitField(input)
//        return aStarThisFieldP1(field)
//    }

    fun part2(input: List<String>): Int {
        val field = inputToDigitField(input)
        return aStarThisFieldP2(field)
    }

//    test(
//        "Part 1 Test 1",
//        """
//            011
//            111
//            111
//        """.trimIndent(),
//        ::part1,
//        4,
//    )
//
//    testFile(
//        "Part 1 Test 1",
//        "Day17_test",
//        ::part1,
//        102,
//    )
//
//    println("Passed tests for Part 1")
    val input = readInput("Day17").filter(String::isNotBlank)
//    part1(input).println()

//    testFile(
//        "Part 2 Test 1",
//        "Day17_test",
//        ::part2,
//        94,
//    )
//    test(
//        "Part 2 Test 1",
//        """
//            011111111111
//            999999999991
//            999999999991
//            999999999991
//            999999999991
//        """.trimIndent(),
//        ::part2,
//        71,
//    )

    measure { part2(input) }
        .println()
}
