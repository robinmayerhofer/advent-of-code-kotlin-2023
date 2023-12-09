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
    val testInput = readInput("Day10_test").filter(String::isNotBlank)
    val testOutput = part1(testInput)
    val expectedTestOutput = 1
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day10").filter(String::isNotBlank)
    part1(input).println()

    val testInput2 = readInput("Day10_test2").filter(String::isNotBlank)
    val testOutput2 = part2(testInput2)
    val expectedTestOutput2 = 1
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }

    val input2 = readInput("Day10_2").filter(String::isNotBlank)
    part2(input2).println()
}