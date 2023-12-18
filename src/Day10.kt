import DirectionDay10.*
import kotlin.math.ceil

private enum class DirectionDay10 {
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

private data class TravelStep(
    val currentPosition: Position,
    val havingCameFrom: DirectionDay10,
)

private data class Pipe(
    val backingChar: Char,
    val from: DirectionDay10,
    val to: DirectionDay10,
    val floodFillColor: Int? = null,
) {
    companion object {
        fun from(char: Char): Pipe = when (char) {
            '|' -> Pipe(char, NORTH, SOUTH) // is a vertical pipe connecting north and south.
            '-' -> Pipe(char, EAST, WEST) // is a horizontal pipe connecting east and west.
            'L' -> Pipe(char, NORTH, EAST) // is a 90-degree bend connecting north and east.
            'J' -> Pipe(char, NORTH, WEST) // is a 90-degree bend connecting north and west.
            '7' -> Pipe(char, SOUTH, WEST) // is a 90-degree bend connecting south and west.
            'F' -> Pipe(char, SOUTH, EAST) // is a 90-degree bend connecting south and east.
            ground.backingChar -> ground // is ground; there is no pipe in this tile.
            'S' -> Pipe(char, ALL, ALL) // is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
            else -> error("Unexpected char '$char'.")
        }

        val ground =  Pipe('.', NONE, NONE)
    }

    fun isStart() = backingChar == 'S'
}

private typealias PipeField = Array<Array<Pipe>>

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
                    return Position(column = columnIndex, row = rowIndex)
                }
            }
        }
        error("Start not found")
    }

    fun PipeField.elementAt(row: Int, column: Int): Pipe =
            this[row][column]

    fun PipeField.elementAt(position: Position): Pipe =
            elementAt(row = position.row, column = position.column)

    fun Position.travel(direction: DirectionDay10): Position =
            when (direction) {
                NORTH -> Position(column = column, row = row - 1)
                EAST -> Position(column = column + 1, row = row)
                SOUTH -> Position(column = column, row = row + 1)
                WEST -> Position(column = column - 1, row = row)
                else -> error("Cannot travel direction '$direction'.")
            }

    fun PipeField.isValidPosition(position: Position): Boolean =
            if (position.column < 0 || position.row < 0) {
                false
            } else if (position.column >= this[0].size || position.row >= this.size) {
                false
            } else {
                true
            }

    fun PipeField.setElementAt(position: Position, element: Pipe) {
        if (!isValidPosition(position)) {
            return
        }
        this[position.row][position.column] = element
    }

    fun PipeField.setElementAtSafe(position: Position, element: Pipe) {
        if (!isValidPosition(position)) {
            return
        }
        setElementAt(position, element)
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
        forcedDirection: DirectionDay10? = null,
    ): TravelStep? {
        if (forcedDirection != null) {
            check(elementAt(currentTravelStep.currentPosition).isStart()) {
                "Forcing a direction is only allowed at the start tile"
            }
        }
        val currentPosition = currentTravelStep.currentPosition
        val havingCameFrom = currentTravelStep.havingCameFrom

        val currentPipe = elementAt(currentPosition)

        val nextDirection = forcedDirection ?: if (currentPipe.from == havingCameFrom) {
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

    // Open loop (first step position != last step position)
    fun PipeField.findLoop(): List<TravelStep> {
        val start: Position = findStart()

        return listOf(NORTH, EAST, SOUTH, WEST).firstNotNullOfOrNull { startDirection ->
            val newPosition: Position = start.travel(direction = startDirection)
            var nextTravelStep = TravelStep(
                    currentPosition = newPosition,
                    havingCameFrom = startDirection.inverse(),
            )
            if (!isValidPosition(nextTravelStep.currentPosition) ||
                    !isValidTravelStep(nextTravelStep)) {
                return@firstNotNullOfOrNull null
            }

            var currentTravelStep: TravelStep = nextTravelStep
            val travelSteps = mutableListOf(nextTravelStep)
            while (!elementAt(travelSteps.last().currentPosition).isStart()) {
                nextTravelStep = next(currentTravelStep) ?: return@firstNotNullOfOrNull null
                travelSteps.add(nextTravelStep)
                currentTravelStep = nextTravelStep
            }
            travelSteps
        } ?: error("Found no loop")
    }

    fun part1(input: List<String>): Int {
        val field = inputToPipeField(input)

        val loopFields: Int = field.findLoop().count()

        return ceil(
                (loopFields.toDouble() / 2.0)
        ).toInt()
    }

    fun PipeField.writeOutput(name: String) = writeOutput(
            name,
            map {
                it.map { pipe ->
                    when (pipe.floodFillColor) {
                        0 -> '█'
                        1 -> '-'
                        else -> ' '
                    }
                }.joinToString("")
            }
    )

    fun PipeField.explodeByThree(): PipeField {
        // should only have loop and none fields at that point
        val rows = size
        val columns = this[0].size

        val newField: Array<Array<Pipe>> = Array(size = rows * 3) {
            Array(size = columns * 3) {
                Pipe.ground
            }
        }

        for (row in 0..<rows) {
            for (column in 0..<columns) {
                val position = Position(row = row, column = column)
                val pipe = elementAt(position)
                val newPosition = Position(row = position.row * 3, column = position.column * 3)
                newField.setElementAtSafe(newPosition, pipe)

                if (pipe.to != NONE && pipe.from != NONE) {
                    newField.setElementAtSafe(
                            newPosition.travel(pipe.to),
                            Pipe(backingChar = '?', from = pipe.to, to = pipe.to.inverse(), floodFillColor = 0)
                    )
                    newField.setElementAtSafe(
                            newPosition.travel(pipe.to).travel(pipe.to),
                            Pipe(backingChar = '?', from = pipe.to, to = pipe.to.inverse(), floodFillColor = 0)
                    )
                    newField.setElementAtSafe(
                            newPosition.travel(pipe.from),
                            Pipe(backingChar = '?', from = pipe.from, to = pipe.from.inverse(), floodFillColor = 0)
                    )
                    newField.setElementAtSafe(
                            newPosition.travel(pipe.from).travel(pipe.from),
                            Pipe(backingChar = '?', from = pipe.from, to = pipe.from.inverse(), floodFillColor = 0)
                    )
                }
            }
        }

        return newField
    }

    fun Position.directionTo(other: Position): DirectionDay10 =
            if (row < other.row) SOUTH
            else if (row > other.row) NORTH
            else if (column < other.column) EAST
            else if (column > other.column) WEST
            else error("This should not happen.")

    fun PipeField.replaceAllNoneLoopFields(loop: List<TravelStep>) {
        val positionsWithLoopPipes = loop.map { it.currentPosition }.toMutableSet()

        this.forEachIndexed { row, pipeRow ->
            pipeRow.forEachIndexed { column, pipe ->
                val position = Position(column = column, row = row)
                if (position !in positionsWithLoopPipes) {
                    this[row][column] = Pipe.ground
                } else {
                    this[row][column] = pipe.copy(floodFillColor = 0)
                }
            }
        }
    }

    fun PipeField.assignDirectionsToStartField(loop: List<TravelStep>) {
        val startPosition = findStart()
        val startTravelablePositions = listOf(
                startPosition.travel(NORTH),
                startPosition.travel(EAST),
                startPosition.travel(SOUTH),
                startPosition.travel(WEST),
        ).filter(::isValidPosition)

        val startPipeNeighbors = loop.mapNotNull { travelStep ->
            val position = travelStep.currentPosition
            val pipe = elementAt(position)

            if (position in startTravelablePositions && (startPosition == position.travel(pipe.from) || startPosition == position.travel(pipe.to))) {
                position
            } else {
                null
            }
        }.also {
            check(it.size == 2)
        }.take(2)

        setElementAt(startPosition, element = elementAt(startPosition)
                .copy(
                        from = startPosition.directionTo(startPipeNeighbors[0]),
                        to = startPosition.directionTo(startPipeNeighbors[1])
                )
        )
    }

    fun PipeField.floodFill() {
        val explodedRows = size
        val explodedColumns = this[0].size
        writeOutput("Day10_02_output_exploded_field")

        val positionsToTry = mutableSetOf<Position>()

        (0..<explodedRows).forEach { row ->
            positionsToTry.add(Position(row = row, column = 0))
            positionsToTry.add(Position(row = row, column = explodedColumns - 1))
        }

        (0..<explodedColumns).forEach { column ->
            positionsToTry.add(Position(column = column, row = 0))
            positionsToTry.add(Position(column = column, row = explodedRows - 1))
        }

        while (positionsToTry.isNotEmpty()) {
            val positionToTry = positionsToTry.first()
            positionsToTry.remove(positionToTry)

            if (!isValidPosition(positionToTry)) {
                continue
            }

            val pipe = elementAt(positionToTry)
            if (pipe.floodFillColor != null) {
                continue
            }

            setElementAt(
                    positionToTry,
                    pipe.copy(floodFillColor = 1)
            )

            val positionsToAdd = listOf(
                    positionToTry.copy(column = positionToTry.column + 1),
                    positionToTry.copy(column = positionToTry.column - 1),
                    positionToTry.copy(row = positionToTry.row + 1),
                    positionToTry.copy(row = positionToTry.row - 1),
            )

            positionsToTry.addAll(positionsToAdd)
        }
    }

    fun PipeField.undoExplosionByThree(): PipeField =
            this
                    .filterIndexed { row, _ -> row % 3 == 0 }
                    .map {
                        it.filterIndexed { column, _ -> column % 3 == 0 }.toTypedArray()
                    }.toTypedArray()

    fun PipeField.countUncolored(): Int =
            this.sumOf { row -> row.count { it.floodFillColor == null } }

    fun part2(input: List<String>): Int {
        val field = inputToPipeField(input)
        val loop: List<TravelStep> = field.findLoop()

        field.replaceAllNoneLoopFields(loop)
        field.assignDirectionsToStartField(loop)
        field.writeOutput("Day10_01_output_only_loop")

        val explodedField = field.explodeByThree()
        explodedField.floodFill()
        explodedField.writeOutput("Day10_03_output_exploded_after_floodfill")

        val unexplodedField = explodedField.undoExplosionByThree()
        unexplodedField.writeOutput("Day10_04_output_normal_after_floodfill")

        return unexplodedField.countUncolored()
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

    fun `test part 1 complex loop`() {
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
    `test part 1 complex loop`()

    val input = readInput("Day10").filter(String::isNotBlank)
    val part1Result = part1(input)
    check(part1Result > 4831) {
        "Part 1 too low ($part1Result should be greater than 4831)"
    }
    part1Result.println()

    fun `test part 2 example 1`() {
        val testInput = """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent().lines().filter(String::isNotBlank)
        val testOutput = part2(testInput)
        val expectedTestOutput = 4
        check(testOutput == expectedTestOutput) {
            "Part 2 Example 1: Expected $expectedTestOutput, got $testOutput"
        }
    }

    fun `test part 2 large example`() {
        val testInput = """
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
        """.trimIndent().lines().filter(String::isNotBlank)
        val testOutput = part2(testInput)
        val expectedTestOutput = 8
        check(testOutput == expectedTestOutput) {
            "Part 2 Large Example: Expected $expectedTestOutput, got $testOutput"
        }
    }

    `test part 2 example 1`()
    `test part 2 large example`()

    val input2 = readInput("Day10").filter(String::isNotBlank)
    val part2Result = part2(input2)
    check(part2Result < 513) {
        "Part 2 too high ($part2Result should be less than 513)"
    }
    part2Result.println()
}