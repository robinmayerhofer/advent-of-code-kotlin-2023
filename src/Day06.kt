import kotlin.coroutines.EmptyCoroutineContext.fold
import kotlin.text.Typography.times

fun main() {

    fun waysToSolve(time: Long, distance: Long): Int =
        (1L..<time).count { accTime ->
            val timeLeft = time - accTime
            val distanceTraveled = timeLeft * accTime
            distanceTraveled > distance
        }

    fun part1(input: List<String>): Int {
        val times = input[0].split(":")[1].findAllNumbers()
        val distances = input[1].split(":")[1].findAllNumbers()

        return times.zip(distances)
            .map { (time, distance) ->
                waysToSolve(time.toLong(), distance.toLong())
            }
            .fold(initial = 1) { a, b -> a * b }
    }

    fun part2(input: List<String>): Int {
        val time = input[0].split(":")[1].replace(" ", "").toLong()
        val distance = input[1].split(":")[1].replace(" ", "").toLong()

        time.println()
        distance.println()

        return waysToSolve(time, distance)
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 288
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day06")
    measure { part1(input) }.println()

    val testOutput2 = part2(testInput)
    val expectedTestOutput2 = 71503
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }

    measure { part2(input) }.println()
}
