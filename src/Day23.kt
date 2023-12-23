import java.time.ZonedDateTime

fun main() {
    shouldLog = true

    val PATH = '.'
    val FOREST = '#'
    val slopeToDirection = mapOf(
        '^' to Direction.NORTH,
        '>' to Direction.EAST,
        'v' to Direction.SOUTH,
        '<' to Direction.WEST,
    )

    fun longestPath(
        field: Field,
        alreadyVisited: Set<Position>,
        current: Position,
        end: Position,
    ): Int? {
        return Direction.entries.mapNotNull { direction ->
            val new = current.travel(direction)

            if (current == end) {
                0
            } else if (!field.isValidPosition(current)) {
                null
            } else if (new in alreadyVisited) {
                null
            } else {
                val element = field[current]
                if (element == FOREST) {
                    null
                } else if (element == PATH || slopeToDirection[element] == direction) {
                    val longestPath =  longestPath(
                        field = field,
                        alreadyVisited = alreadyVisited + current,
                        current = new,
                        end = end,
                    )
                    if (longestPath == null) {
                        null
                    } else {
                        1 + longestPath
                    }
                } else {
                    null
                }
            }
        }.maxOrNull()
    }

    fun part1(input: List<String>): Int {
        val field = inputToField(input)
        val start = Position(row = 0, column = 1)
        val end = Position(row = field.size - 1, column = field[0].size - 2)
        return longestPath(
            field = field,
            alreadyVisited = emptySet(),
            current = start,
            end = end,
        )!!
    }

    fun part2(input: List<String>): Int {
        val field = inputToField(input)
            .map { row ->
                row.map {
                    if (it in slopeToDirection.keys) {
                        PATH
                    } else {
                        it
                    }
                }.toCharArray()
            }.toTypedArray()


        val start = Position(row = 0, column = 1)
        val end = Position(row = field.size - 1, column = field[0].size - 2)
        return longestPath(
            field = field,
            alreadyVisited = emptySet(),
            current = start,
            end = end,
        )!!
    }

//    testFile(
//        "Part 1 Test 1",
//        "Day23_test",
//        ::part1,
//        94,
//        filterBlank = false,
//    )
//
//    val input = readInput("Day23").filter(String::isNotBlank)
//    measure { part1(input) }
//        .also { check(it == 2170) }
//        .println()

    testFile(
        "Part 2 Test 1",
        "Day23_test",
        ::part2,
        154,
        filterBlank = false,
    )
    println(ZonedDateTime.now())
    val input2 = readInput("Day23").filter(String::isNotBlank)
    part2(input2).println()
}