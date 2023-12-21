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

        var depth = 0
        while (depth <= maxDepth) {
            map[depth + 1] = map[depth]!!.flatMap { position: Position ->
                Direction.entries.mapNotNull { direction ->
                    val newPosition = position.travel(direction)
                    if (field.isValidAndNotARock(newPosition)) {
                        newPosition
                    } else {
                        null
                    }
                }
            }.toSet()

            depth += 1
        }

        return map[maxDepth]!!.size
    }

    fun part1(input: List<String>, steps: Int): Int {
        val field = inputToField(input)
        val startPosition = field.find { it == START }
        field[startPosition] = GARDEN
        return breadthFirst(field, startPosition, steps)
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
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
//        "Part 2 Test 1",
//        "Day21_test",
//        ::part2,
//        1,
//    )
//    val input2 = readInput("Day21").filter(String::isNotBlank)
//    part2(input2).println()
}
