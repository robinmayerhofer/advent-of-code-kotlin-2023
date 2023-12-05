import kotlin.math.pow

fun main() {

    val numberRegex = "(\\d+)".toRegex()

    fun findWinningNumbersSetSize(line: String): Int {
        val (winning, actual) = line.split(":")[1].split("|")
        val winningSet = numberRegex.findAll(winning).map { it.groups.first()!!.value.toInt() }.toSet()
        val actualSet = numberRegex.findAll(actual).map { it.groups.first()!!.value.toInt() }.toSet()

        return winningSet.intersect(actualSet).size
    }

    fun part1(input: List<String>): Int =
            input.sumOf { line ->
                val size = findWinningNumbersSetSize(line)
                if (size == 0) {
                    0
                } else {
                    2.toDouble().pow((size - 1).toDouble()).toInt()
                }
            }

    fun part2(input: List<String>): Int {
        val amountOfGamesPerCard = MutableList(input.size) { 1 }

        return input.mapIndexed { index, line ->
            val winAmount = findWinningNumbersSetSize(line)
            val numberOfGamesCurrentCard = amountOfGamesPerCard[index]

            for (i in 1..winAmount) {
                val numberOfGamesFutureCard = amountOfGamesPerCard[index + i]
                amountOfGamesPerCard[index + i] = numberOfGamesFutureCard + numberOfGamesCurrentCard
            }

            numberOfGamesCurrentCard
        }.sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 13
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day04")
    part1(input).println()

    val testOutput2 = part2(testInput)
    val expectedTestOutput2 = 30
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }
    part2(input).println()
}
