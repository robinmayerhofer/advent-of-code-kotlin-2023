import kotlin.math.abs

fun main() {

    fun expandUniverse(input: List<String>): List<String> {
        val verticallyExpanded = input.flatMap {  line ->
            if (line.all { it == '.' }) {
                listOf(line, line)
            } else {
                listOf(line)
            }
        }

        val horizontalIndicesToExpand = input[0].indices.filter { index ->
            input.all { line -> line[index] == '.' }
        }.toSet()

        return verticallyExpanded.map { line ->
            line.flatMapIndexed { index, char ->
                if (index in horizontalIndicesToExpand) {
                    listOf(char, char)
                } else {
                    listOf(char)
                }
            }
        }.map { it.joinToString("") }
    }

    fun expandUniverse2(input: List<String>): Pair<Set<Int>, Set<Int>> {
        val verticalIndicesToExpand = input.indices.filter { index ->
            input[index].all { char -> char == '.' }
        }.toSet()

        val horizontalIndicesToExpand = input[0].indices.filter { index ->
            input.all { line -> line[index] == '.' }
        }.toSet()

        return Pair(verticalIndicesToExpand, horizontalIndicesToExpand)
    }

    fun findGalaxies(universe: List<String>): Set<Pair<Int, Int>> =
        universe.flatMapIndexed { row: Int, line: String ->
            line.mapIndexedNotNull { column, c ->
                if (c == '#') {
                    row to column
                } else {
                    null
                }
            }
        }.toSet()

    fun part1(input: List<String>): Int {
        val universe = expandUniverse(input)
        val galaxies: Set<Pair<Int, Int>> = findGalaxies(universe)

        return galaxies.flatMapIndexed { i1, (y1, x1) ->
            galaxies.mapIndexed { i2, (y2, x2) ->
                if (i2 <= i1) { 0 }
                else { abs(y1 - y2) + abs(x1 - x2) }
            }
        }.sum()
    }

    fun createRange(a: Int, b: Int): IntRange =
            if (a < b) {
                a..b
            } else {
                b..a
            }

    fun part2(input: List<String>, expansionFactor: Long): Long {
        val universe = input
        val (verticalIndicesToExpand, horizontalIndicesToExpand) = expandUniverse2(input)
        val galaxies: Set<Pair<Int, Int>> = findGalaxies(universe)

        return galaxies.flatMapIndexed { i1, (y1, x1) ->
            galaxies.mapIndexed { i2, (y2, x2) ->
                if (i2 <= i1) { 0 }
                else {
                    val part1 = abs(y1 - y2) + abs(x1 - x2)

                    val xRange = createRange(x1, x2)
                    val yRange = createRange(y1, y2)

                    val xCount = xRange.count { x -> x in horizontalIndicesToExpand }
                    val yCount = yRange.count { y -> y in verticalIndicesToExpand }

                    (part1 - xCount - yCount).toLong() + xCount*expansionFactor + yCount*expansionFactor
                }
            }
        }.sum()
    }

    test(
            "Part 1 Test 1",
            "Day11_test",
            ::part1,
            374
    )

    val input = readInput("Day11").filter(String::isNotBlank)
    part1(input).println()

    test(
            "Part 2 Test with expansion factor 10",
            "Day11_test",
            { input -> part2(input, 10L) },
            1030L,
    )
    test(
            "Part 2 Test with expansion factor 10",
            "Day11_test",
            { input -> part2(input, 100L) },
            8410L,
    )
    val input2 = readInput("Day11").filter(String::isNotBlank)
    measure { part2(input2, 1_000_000L) }.println()
}
