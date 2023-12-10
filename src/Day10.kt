import Direction.*
import kotlin.math.ceil

enum class Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST,
    ALL,
    NONE,
    ;

    fun inverse() = when (this) {
        NORTH -> SOUTH
        EAST -> WEST
        SOUTH -> NORTH
        WEST -> EAST
        NONE -> error("")
        ALL -> error("")
    }
}

data class Position(val x: Int, val y: Int)

data class TravelStep(
        val currentPosition: Position,
        val havingCameFrom: Direction,
)

data class Pipe(
        val backingChar: Char,
        val from: Direction,
        val to: Direction,
) {
    companion object {
        fun from(char: Char): Pipe = when (char) {
            '|' -> Pipe(char, NORTH, SOUTH) // is a vertical pipe connecting north and south.
            '-' -> Pipe(char, EAST, WEST) // is a horizontal pipe connecting east and west.
            'L' -> Pipe(char, NORTH, EAST) // is a 90-degree bend connecting north and east.
            'J' -> Pipe(char, NORTH, WEST) // is a 90-degree bend connecting north and west.
            '7' -> Pipe(char, SOUTH, WEST) // is a 90-degree bend connecting south and west.
            'F' -> Pipe(char, SOUTH, EAST) // is a 90-degree bend connecting south and east.
            '.' -> Pipe(char, NONE, NONE) // is ground; there is no pipe in this tile.
            'S' -> Pipe(char, ALL, ALL) // is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
            else -> error("Unexpected char '$char'.")
        }
    }

    fun isStart() = backingChar == 'S'
}

typealias PipeField = Array<Array<Pipe>>

fun main() {

    fun inputToPipeField(input: List<String>) =
            inputToField(input)
                    .map { row ->
                        row.map { column ->
                            Pipe.from(column)
                        }.toTypedArray()
                    }.toTypedArray()

    fun PipeField.findStart(): Position {
        for ((rowIndex, row) in this.withIndex()) {
            for ((columnIndex, pipe) in row.withIndex()) {
                if (pipe.isStart()) {
                    return Position(x = columnIndex, y = rowIndex)
                }
            }
        }
        error("Start not found")
    }

    fun PipeField.elementAt(position: Position): Pipe =
            this[position.y][position.x]

    fun Position.travel(direction: Direction): Position =
            when (direction) {
                NORTH -> Position(x = x, y = y - 1)
                EAST -> Position(x = x + 1, y = y)
                SOUTH -> Position(x = x, y = y + 1)
                WEST -> Position(x = x - 1, y = y)
                else -> error("Cannot travel direction '$direction'.")
            }

    fun PipeField.isValidPosition(position: Position): Boolean =
            if (position.x < 0 || position.y < 0) {
                false
            } else if (position.x >= this[0].size || position.y >= this.size) {
                false
            } else {
                true
            }

    fun PipeField.isValidTravelStep(travelStep: TravelStep): Boolean {
        val pipe = elementAt(travelStep.currentPosition)
        if (pipe.isStart()) {
            return true
        }
        return when (travelStep.havingCameFrom) {
            pipe.from -> true
            pipe.to -> true
            else -> false
        }
    }

    fun PipeField.next(
            currentTravelStep: TravelStep,
    ): TravelStep? {
        val currentPosition = currentTravelStep.currentPosition
        val havingCameFrom = currentTravelStep.havingCameFrom

        val currentPipe = elementAt(currentPosition)

        val nextDirection = if (currentPipe.from == havingCameFrom) {
            currentPipe.to
        } else if (currentPipe.to == havingCameFrom) {
            currentPipe.from
        } else {
            error("Invalid")
        }

        return currentPosition
                .travel(nextDirection)
                .takeIf { isValidPosition(it) }
                ?.let { newPosition ->
                    TravelStep(
                            currentPosition = newPosition,
                            havingCameFrom = nextDirection.inverse()
                    )
                }
                ?.takeIf { travelStep ->
                    isValidTravelStep(travelStep)
                }
    }

    fun part1(input: List<String>): Int {
        val field = inputToPipeField(input)
        val start: Position = field.findStart()

        val loopLength: Int = listOf(NORTH, EAST, SOUTH, WEST).mapNotNull { startDirection ->
            val newPosition: Position = start.travel(direction = startDirection)
            var nextTravelStep = TravelStep(
                    currentPosition = newPosition,
                    havingCameFrom = startDirection.inverse(),
            )
            if (!field.isValidPosition(nextTravelStep.currentPosition) ||
                    !field.isValidTravelStep(nextTravelStep)) {
                return@mapNotNull null
            }

            var length = 1
            var currentTravelStep: TravelStep
            val travelSteps = mutableListOf(nextTravelStep)
            while (true) {
                currentTravelStep = nextTravelStep
                if (field.elementAt(currentTravelStep.currentPosition).isStart()) {
                    // println("Found loop length $length: $travelSteps")
                    return@mapNotNull length
                }
                nextTravelStep = field.next(currentTravelStep) ?: return@mapNotNull null
                travelSteps.add(nextTravelStep)
                length += 1

            }
            error("Goodbye")
        }.max()

        return ceil(
                (loopLength.toDouble() / 2.0)
        ).toInt()
    }

    fun part2(input: List<String>): Int =
            input.sumOf {
                it.length
            }

    // test if implementation meets criteria from the description, like:
    fun `test part 1 simple loop`() {
        val testInput = """
            -L|F7
            7S-7|
            L|7||
            -L-J|
            L|-JF
        """.trimIndent().lines().filter(String::isNotBlank)
        val testOutput = part1(testInput)
        val expectedTestOutput = 4
        check(testOutput == expectedTestOutput) {
            "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
        }
    }

    fun `test part 2 complex loop`() {
        val testInput = """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
        """.trimIndent().lines().filter(String::isNotBlank)
        val testOutput = part1(testInput)
        val expectedTestOutput = 8
        check(testOutput == expectedTestOutput) {
            "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
        }
    }

    `test part 1 simple loop`()
    `test part 2 complex loop`()

    val input = readInput("Day10").filter(String::isNotBlank)
    val part1Result = part1(input)
    check(part1Result > 4831) {
        "Part 1 too low ($part1Result should be greater than 4831)"
    }
    part1Result.println()

//    val testInput2 = readInput("Day10_test2").filter(String::isNotBlank)
//    val testOutput2 = part2(testInput2)
//    val expectedTestOutput2 = 1
//    check(testOutput2 == expectedTestOutput2) {
//        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
//    }
//
//    val input2 = readInput("Day10_2").filter(String::isNotBlank)
//    part2(input2).println()
}