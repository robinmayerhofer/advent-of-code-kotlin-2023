import java.util.*



fun main() {

    data class LastDirection(
        val dir: Direction,
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

    fun dijkstraPart1(field: DigitField): Int {
        val priorityQueue = PriorityQueue<CacheValue>()
        val cache: MutableMap<Pair<Position, Direction>, CacheValue> = mutableMapOf()

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
                previous = null,
            )
        )
        while (priorityQueue.isNotEmpty()) {
            val current = priorityQueue.poll()

            if (!field.isValidPosition(current.position)) {
                continue
            }
            if (current.position == startPosition && current.lastDirection != null) {
                continue
            }

            val costOfPosition = field[current.position]
            if (current.position == endPosition) {
                return current.costUntilPreviousPosition + costOfPosition
            }

            if (current.lastDirection != null) {
                val cachedValue: CacheValue? = cache[current.position to current.lastDirection.dir]
                if (cachedValue != null) {
                    if (cachedValue.lastDirection != null) {
                        if (cachedValue.lastDirection.amount <= current.lastDirection.amount) {
                            if (cachedValue.costUntilPreviousPosition <= current.costUntilPreviousPosition) {
                                continue
                            }
                        }
                    }
                }

                cache[current.position to current.lastDirection.dir] = current
            }

            val forbiddenDirections = current.lastDirection?.dir?.reverse()?.let { mutableListOf(it) }
                ?: mutableListOf()
            if (current.lastDirection?.amount == 3) {
                // should not go forward
                forbiddenDirections.add(current.lastDirection.dir)
            }

            for (direction in Direction.entries) {
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
                    lastDirection = newLastDirection,
                    previous = current,
                )
                priorityQueue.add(next)
            }
        }

        error("Oops, found no end")
    }

    fun CacheValue.printPath() {
        val positions = mutableListOf<Position>()

        var current: CacheValue? = this
        while (current != null) {
            positions.add(current.position)
            current = current.previous
        }
        positions.reversed().println()
    }

    fun dijkstraPart2(field: DigitField): Int {
        val priorityQueue = PriorityQueue<CacheValue>()
        val cache: MutableMap<Pair<Position, Direction>, CacheValue> = mutableMapOf()

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
            if (!field.isValidPosition(current.position)) {
                continue
            }
            if (current.position == startPosition && current.lastDirection != null) {
                continue
            }

            val costOfPosition = field[current.position]
            if (current.position == endPosition && (current.lastDirection?.amount ?: 0) >= 4) {
                println("Found path to end:")
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
                                continue
                            }
                        }
                    }
                }

                if (current.lastDirection.amount >= 4) {
                    cache[current.position to current.lastDirection.dir] = current
                }
            }


            val allowedDirections = mutableSetOf<Direction>()
            if (current.lastDirection != null) {
                if (current.lastDirection.amount < 10) {
                    allowedDirections.add(current.lastDirection.dir)
                }

                if (current.lastDirection.amount >= 4) {
                    allowedDirections.addAll(
                        Direction.entries
                            .toList()
                            .filter { it != current.lastDirection.dir && it != current.lastDirection.dir.reverse() }
                    )
                }
            } else {
                allowedDirections.addAll(Direction.entries.toList())
            }

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


    fun part1(input: List<String>): Int {
        val field = inputToDigitField(input)
        return dijkstraPart1(field)
    }

    fun part2(input: List<String>): Int {
        val field = inputToDigitField(input)
        return dijkstraPart2(field)
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
    measure { part1(input) }
        .println()

    testFile(
        "Part 2 Test 1",
        "Day17_test",
        ::part2,
        94,
    )
    test(
        "Part 2 Test 1",
        """
            011111111111
            999999999991
            999999999991
            999999999991
            999999999991
        """.trimIndent(),
        ::part2,
        71,
    )

    measure { part2(input) }
        .println()
}
