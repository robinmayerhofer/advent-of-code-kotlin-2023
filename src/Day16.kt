fun main() {

    fun part1(input: List<String>): Long =
            input.sumOf {
                it.length.toLong()
            }

    fun part2(input: List<String>): Long =
            input.sumOf {
                it.length.toLong()
            }

    testFile(
            "Part 1 Test 1",
            "Day16_test",
            ::part1,
            1
    )

    val input = readInput("Day16").filter(String::isNotBlank)
    part1(input).println()

    testFile(
            "Part 2 Test 1",
            "Day16_test",
            ::part2,
            1
    )
    val input2 = readInput("Day16_2").filter(String::isNotBlank)
    part2(input2).println()
}