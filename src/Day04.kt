fun main() {

    fun part1(input: List<String>): Int =
            input.sumOf {
                it.length
            }

    fun part2(input: List<String>): Int =
            input.sumOf {
                it.length
            }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 1
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day04")
    part1(input).println()

    val testInput2 = readInput("Day04_test2")
    val testOutput2 = part2(testInput2)
    val expectedTestOutput2 = 1
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }
    part2(input).println()
}