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

    fun isXMirror(field: Field, mirrorPosition: Int): Boolean {
        // mirrorPosition = 1 => between 0 and 1

        val minX = 0
        val maxX = field[0].size - 1

        for (rowDelta in 0..<maxX) {
            val r1 = mirrorPosition - rowDelta - 1
            val r2 = mirrorPosition + rowDelta

            if (r1 < minX || r2 > maxX) {
                break
            }

            val column1 = field.map { row -> row[r1] }
            val column2 = field.map { row -> row[r2] }
            if (column1 != column2) {
                return false
            }
        }

        return true
    }

    fun isYMirror(field: Field, mirrorPosition: Int): Boolean {
        // mirrorPosition = 1 => between 0 and 1

        val minY = 0
        val maxY = field.size - 1

        for (columnDelta in 0..<maxY) {
            val c1 = mirrorPosition - columnDelta - 1
            val c2 = mirrorPosition + columnDelta

            if (c1 < minY || c2 > maxY) {
                break
            }

            val column1 = field[c1]
            val column2 = field[c2]
            if (!column1.contentEquals(column2)) {
                return false
            }
        }

        return true
    }

    fun solvePuzzle(field: Field): Long {
        val xRange = 1..<field[0].size
        val yRange = 1..<field.size

        for (x in xRange) {
            if (isXMirror(field, x)) {
                println("Found X mirror")
                return x.toLong()
            }
        }

        for (y in yRange) {
            println("Found Y mirror")
            if (isYMirror(field, y)) {
                return y * 100L
            }
        }

        println("Found field with no mirror:")
        println(field.joinToString("\n") { it.joinToString("") })
        error("No mirror found")
    }

    fun part1(input: List<String>): Long {
        val puzzles = readInput(input)

        return puzzles
            .sumOf { solvePuzzle(it) }
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

//    testFile(
//        "Part 1 Test 1",
//        "Day13_test",
//        ::part1,
//        405L,
//        filterBlank = false
//    )

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
//
//    testFile(
//        "Part 2 Test 1",
//        "Day13_test",
//        ::part2,
//        1,
//        filterBlank = false
//    )
//    val input2 = readInput("Day13_2").filter(String::isNotBlank)
//    part2(input2).println()
}
