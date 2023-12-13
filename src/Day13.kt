fun main() {

    fun readInput(input: List<String>): List<Field> =
        input.fold(initial = mutableListOf(mutableListOf<String>())) { acc, line ->
            if (line.isBlank()) {
                acc.add(mutableListOf())
            } else {
                acc.last().add(line)
            }
            acc
        }
            .filter { it.isNotEmpty() }
            .map { inputToField(it) }

    fun isYMirror(field: Field, mirrorPosition: Int, smudged: Boolean): Boolean {
        // mirrorPosition = 1 => between 0 and 1

        val minY = 0
        val maxY = field.size - 1

        val expectedDiffs = if (smudged) 1 else 0
        var diffs = 0

        for (columnDelta in 0..<maxY) {
            val c1 = mirrorPosition - columnDelta - 1
            val c2 = mirrorPosition + columnDelta

            if (c1 < minY || c2 > maxY) {
                break
            }

            val row1 = field[c1]
            val row2 = field[c2]
            diffs += row1.zip(row2).count { (a, b) -> a != b }
            if (diffs > expectedDiffs) {
                return false
            }
        }

        return diffs == expectedDiffs
    }

    fun solvePuzzle(field: Field, smudged: Boolean = false): Long {
        val xRange = 1..<field[0].size
        val yRange = 1..<field.size

        for (y in yRange) {
            if (isYMirror(field, y, smudged)) {
                return y * 100L
            }
        }

        val transposedField = transpose(field)

        for (x in xRange) {
            if (isYMirror(transposedField, x, smudged)) {
                return x.toLong()
            }
        }
        error("No mirror found")
    }

    fun part1(input: List<String>): Long {
        val puzzles = readInput(input)

        return puzzles
            .sumOf { solvePuzzle(it) }
    }

    fun part2(input: List<String>): Long {
        val puzzles = readInput(input)

        return puzzles
            .sumOf { solvePuzzle(it, smudged = true) }
    }


    testFile(
        "Part 1 Test 1",
        "Day13_test",
        ::part1,
        405L,
        filterBlank = false
    )

    test(
        "Part 1 Failing thing",
        """
            .##.###.#####
            .##.#..#.....
            ##.###.....##
            #####..#...##
            #####.....###
            .##..##.#.#..
            ......##.....
        """.trimIndent(),
            ::part1,
            expectedValue = 12L
    )


    val input = readInput("Day13")
    part1(input)
        .also { check(it > 24519) { "Too low. $it should be > 24519" } }
        .println()


    test(
        "Part 2 Example 1",
        """
            #.##..##.
            ..#.##.#.
            ##......#
            ##......#
            ..#.##.#.
            ..##..##.
            #.#.##.#.
        """.trimIndent(),
        ::part2,
        expectedValue = 300L
    )

    test(
        "Part 2 Example 2",
        """
            #...##..#
            #....#..#
            ..##..###
            #####.##.
            #####.##.
            ..##..###
            #....#..#
        """.trimIndent(),
        ::part2,
        expectedValue = 100L
    )


    // 21512 too low
    // 30879 too high
    // 36822 incorrect

    // => 26195? not right
    part2(input).println()
}
