fun main() {
    shouldLog = true

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
        "Day21_test",
        ::part1,
        1,
        filterBlank = false,
    )

    val input = readInput("Day21").filter(String::isNotBlank)
    part1(input).println()

    testFile(
        "Part 2 Test 1",
        "Day21_test",
        ::part2,
        1,
        filterBlank = false,
    )
    val input2 = readInput("Day21").filter(String::isNotBlank)
    part2(input2).println()
}
