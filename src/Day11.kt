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

        println("horizontalIndicesToExpand: $horizontalIndicesToExpand")

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

    fun part1(input: List<String>): Int {
        val universe = expandUniverse(input)
        val galaxies: Set<Pair<Int, Int>> = universe.flatMapIndexed { row: Int, line: String ->
            line.mapIndexedNotNull { column, c ->
                if (c == '#') {
                    row to column
                } else {
                    null
                }
            }
        }.toSet()

        return galaxies.flatMapIndexed { i1, (y1, x1) ->
            galaxies.mapIndexed { i2, (y2, x2) ->
                if (i2 <= i1) { 0 }
                else { abs(y1 - y2) + abs(x1 - x2) }
            }
        }.sum()
    }


    fun part2(input: List<String>): Int =
            input.sumOf {
                it.length
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
            "Part 2 Test 1",
            "Day11_test",
            ::part2,
            1
    )
    val input2 = readInput("Day11_2").filter(String::isNotBlank)
    part2(input2).println()
}