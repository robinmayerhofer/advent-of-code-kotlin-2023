fun main() {

    fun buildHierarchy(line: String): List<List<Int>> {
        val numbers = line.findAllNumbers()
        val hierarchy = mutableListOf(numbers)

        while (hierarchy.last().any { it != 0 } && hierarchy.last().size > 1) {
            val current = hierarchy.last()
            val next = current.drop(1).zip(current.dropLast(1))
                    .map { (a, b) -> a - b }
            hierarchy.add(next)
        }
        return hierarchy
    }

    fun predict(line: String, atEnd: Boolean = true): Int {
        val hierarchy = buildHierarchy(line)

        var addedPreviousLine = 0
        for (hierarchyLine in hierarchy.dropLast(1).reversed()) {
            if (atEnd) {
                addedPreviousLine += hierarchyLine.lastOrNull() ?: 0
            } else {
                addedPreviousLine = (hierarchyLine.firstOrNull() ?: 0) - addedPreviousLine
            }
        }

        return addedPreviousLine
    }

    fun part1(input: List<String>): Int =
            input.sumOf {
                predict(it)
            }

    fun part2(input: List<String>): Int =
            input.sumOf {
                predict(it, atEnd = false)
            }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test").filter(String::isNotBlank)
    val testOutput = part1(testInput)
    val expectedTestOutput = 114
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }
    println("Part 1 Tests passed")

    val input = readInput("Day09").filter(String::isNotBlank)
    val output = part1(input)
    output.println()

    val t2 = predict("10  13  16  21  30  45", atEnd = false)
    check(t2 == 5) { "Part2 T1 failed, got $t2"}
    part2(input).println()
}