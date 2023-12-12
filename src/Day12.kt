typealias Configuration = List<Char>
typealias Requirements = List<Int>

private val OPERATIONAL = '.'
private val DAMAGED = '#'
private val UNKKNOWN = '?'

private fun advanceRequirements(chars: Configuration, requirements: Requirements): Int {
    if (requirements.isEmpty()) {
        return 0
    }

    val expectedDamagedSprings = requirements[0]
    if (chars.size < expectedDamagedSprings) {
        return 0
    }

    val enoughDamagedSpringsInARow = ( 0..<expectedDamagedSprings).all {
        chars.getOrNull(it) != OPERATIONAL
    }

    return if (!enoughDamagedSpringsInARow) {
        0
    } else if (expectedDamagedSprings == chars.size) {
        if (requirements.size == 1) {
            1
        } else {
            0
        }
    } else if (chars[expectedDamagedSprings] == DAMAGED) {
        0
    } else {
        // dropping 1 extra char allows to skip the branching when there is a unknown spring (it has to be operational)
        solutions(chars.drop(expectedDamagedSprings+1), requirements.drop(1))
    }
}

private fun solutions(chars: Configuration, requirements: Requirements): Int {
    if (chars.isEmpty()) {
        return when {
            requirements.isEmpty() -> 1
            else -> 0
        }
    }

    return when (chars.first()) {
        OPERATIONAL -> solutions(chars.drop(1), requirements)
        UNKKNOWN -> solutions(chars.drop(1), requirements) + advanceRequirements(chars, requirements)
        DAMAGED -> advanceRequirements(chars, requirements)
        else -> error("Invalid input")
    }
}

fun main() {

    fun parseLine(line: String): Pair<Configuration, Requirements> {
        val (configuration, requirements) = line.split(" ")

        return configuration.toList() to requirements.findAllNumbers()
    }

    fun part1(input: List<String>): Int =
        input.sumOf { line ->
            val (config, req) = parseLine(line)
            solutions(config, req)
        }

    fun part2(input: List<String>): Int =
        input.sumOf { line ->
            val (config, req) = parseLine(line)

            solutions(
                (0..<5).joinToString("?") { config.joinToString("") }.toList(),
                req,
            )
        }

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

    val input = readInput("Day12").filter(String::isNotBlank)
    part1(input).println()

    val input2 = readInput("Day12").filter(String::isNotBlank)
    part2(input2).println()
}
