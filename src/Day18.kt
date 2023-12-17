fun main() {

    fun part1(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    testFile(
        "Part 1 Test 1",
        "Day18_test",
        ::part1,
        1
    )

    val input = readInput("Day18").filter(String::isNotBlank)
    part1(input).println()

    testFile(
        "Part 2 Test 1",
        "Day18_test",
        ::part2,
        1
    )
    val input2 = readInput("Day18_2").filter(String::isNotBlank)
    part2(input2).println()
}