typealias Configuration = List<Char>
typealias Requirements = List<Int>

private val OPERATIONAL = '.'
private val DAMAGED = '#'
private val UNKKNOWN = '?'

val memory = mutableMapOf<Pair<Requirements, Configuration>, Long>()

private fun advanceRequirements(chars: Configuration, requirements: Requirements): Long {
    memory[requirements to chars]?.let { return it }

    if (requirements.isEmpty()) {
        return 0
    }

    val expectedDamagedSprings = requirements[0]
    if (chars.size < expectedDamagedSprings) {
        return 0
    }

    val notEnoughDamagedSpringsInARow = (0..<expectedDamagedSprings).any {
        chars[it] == OPERATIONAL
    }

    if (notEnoughDamagedSpringsInARow) {
        return 0
    }

    return if (expectedDamagedSprings == chars.size) {
        // At the end => requirements left?
        if (requirements.size == 1) { 1 }
        else { 0 }
    } else if (chars[expectedDamagedSprings] == DAMAGED) {
        // There are too many damaged springs, after a requirement for damaged spring there must be an operational one
        0
    } else {
        // dropping 1 extra char allows to skip the branching when there is a unknown spring (it has to be operational)
        solutions(chars.drop(expectedDamagedSprings + 1), requirements.drop(1))
    }
}

private fun solutions(chars: Configuration, requirements: Requirements): Long {
    if (chars.isEmpty()) {
        return when {
            requirements.isEmpty() -> 1
            else -> 0
        }
    }

    return when (chars.first()) {
        OPERATIONAL -> solutions(chars.drop(1), requirements)
        DAMAGED -> advanceRequirements(chars, requirements).also { memory[requirements to chars] = it }
        UNKKNOWN -> solutions(chars.drop(1), requirements) + advanceRequirements(
            chars,
            requirements
        ).also { memory[requirements to chars] = it }

        else -> error("Invalid input")
    }
}


fun main() {

    fun part1(input: List<String>): Long {
        return input.sumOf { line ->
            val (configuration, requirements) = line.split(" ")

            solutions(
                configuration.toList(),
                requirements.findAllNumbers(),
            )
        }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { line ->
            val (config, requirements) = line.split(" ")

            val unfoldedConfig = (0..<5).joinToString("?") { config }.toList()
            val unfoldedRequirements = (0..<5).joinToString(",") { requirements }.findAllNumbers()
            solutions(
                unfoldedConfig,
                unfoldedRequirements,
            ).also {
//                "$it solutions for ${unfoldedConfig.joinToString("")} ${unfoldedRequirements.joinToString(",")}".println()
            }
        }
    }

    fun testsPart1() {
        test(
            "Part 1 Test 1",
            "???.### 1,1,3",
            ::part1,
            1
        )
        test(
            "Part 1 Test 2",
            ".??..??...?##. 1,1,3",
            ::part1,
            4
        )
        test(
            "Part 1 Test 3",
            "?#?#?#?#?#?#?#? 1,3,1,6",
            ::part1,
            1
        )
        test(
            "Part 1 Test 4",
            "????.#...#... 4,1,1",
            ::part1,
            1
        )
        test(
            "Part 1 Test 5",
            "????.######..#####. 1,6,5",
            ::part1,
            4
        )
        test(
            "Part 1 Test 6",
            "?###???????? 3,2,1",
            ::part1,
            10
        )
        testFile(
            "Part 1 Test File",
            "Day12_test",
            ::part1,
            21
        )
    }

    testsPart1()
    val input = readInput("Day12").filter(String::isNotBlank)
    memory.clear()
    part1(input).println()

    fun testsPart2() {
        test(
            "Part 2 Test 1",
            "???.### 1,1,3",
            ::part2,
            1
        )
        test(
            "Part 2 Test 2",
            ".??..??...?##. 1,1,3",
            ::part2,
            16384
        )
        test(
            "Part 2 Test 3",
            "?#?#?#?#?#?#?#? 1,3,1,6",
            ::part2,
            1
        )
        test(
            "Part 2 Test 4",
            "????.#...#... 4,1,1",
            ::part2,
            16
        )
        test(
            "Part 2 Test 5",
            "????.######..#####. 1,6,5",
            ::part2,
            2500
        )
        test(
            "Part 2 Test 6",
            "?###???????? 3,2,1",
            ::part2,
            506250
        )
        testFile(
            "Part 2 Test File",
            "Day12_test",
            ::part2,
            525152
        )
    }

    testsPart2()
    val input2 = readInput("Day12").filter(String::isNotBlank)

    memory.clear()
    measure { part2(input2) }
        .println()
}
