import TravelingDirection.*
import java.util.*

enum class TravelingDirection {
    UPWARDS,
    DOWNWARDS,
    TO_RIGHT,
    TO_LEFT,
    ;

    fun isHorizontallyTravelling(): Boolean =
            when (this) {
                UPWARDS, DOWNWARDS -> false
                TO_RIGHT, TO_LEFT -> true
            }

    fun isVerticallyTravelling(): Boolean =
            when (this) {
                UPWARDS, DOWNWARDS -> true
                TO_RIGHT, TO_LEFT -> false
            }

    fun splitterA(): TravelingDirection =
            when (this) {
                UPWARDS -> TO_RIGHT
                TO_RIGHT -> UPWARDS
                DOWNWARDS -> TO_LEFT
                TO_LEFT -> DOWNWARDS
            }

    fun splitterB(): TravelingDirection =
            when (this) {
                UPWARDS -> TO_LEFT
                TO_LEFT -> UPWARDS
                DOWNWARDS -> TO_RIGHT
                TO_RIGHT -> DOWNWARDS
            }

}

const val EMPTY_SPACE = '.'
const val SPLITTER_WHEN_HORIZONTALLY_TRAVELLING = '|'
const val SPLITTER_WHEN_VERTICALLY_TRAVELLING = '-'
const val DIRECTING_SPLITTER_A = '/'
const val DIRECTING_SPLITTER_B = '\\'

fun main() {

    data class Position(val column: Int, val row: Int)

    fun Field.isValidPosition(position: Position): Boolean =
            if (position.column < 0 || position.row < 0) {
                false
            } else if (position.column >= this[0].size || position.row >= this.size) {
                false
            } else {
                true
            }

    fun Position.travel(direction: TravelingDirection): Position =
            when (direction) {
                TO_LEFT -> Position(column = column - 1, row = row)
                TO_RIGHT -> Position(column = column + 1, row = row)
                UPWARDS -> Position(column = column, row = row - 1)
                DOWNWARDS -> Position(column = column, row = row + 1)
            }

    fun Field.elementAt(row: Int, column: Int): Char =
            this[row][column]

    fun Field.elementAt(position: Position): Char =
            elementAt(row = position.row, column = position.column)

    fun Field.shootBeam(queue: LinkedList<Pair<Position, TravelingDirection>>,
                        cache: MutableSet<Pair<Position, TravelingDirection>>,
                        currentPosition: Position,
                        direction: TravelingDirection,
                        allVisitedPositions: MutableSet<Position>) {
        if (!this.isValidPosition(currentPosition)) {
            return
        }

        if (cache.contains(currentPosition to direction)) {
            return
        }

        allVisitedPositions.add(currentPosition)
        cache.add(currentPosition to direction)

        when (elementAt(currentPosition)) {
            EMPTY_SPACE -> queue.add(currentPosition.travel(direction) to direction)
            SPLITTER_WHEN_HORIZONTALLY_TRAVELLING -> if (direction.isHorizontallyTravelling()) {
                queue.add(currentPosition.travel(UPWARDS) to UPWARDS)
                queue.add(currentPosition.travel(DOWNWARDS) to DOWNWARDS)
            } else {
                queue.add(currentPosition.travel(direction) to direction)
            }

            SPLITTER_WHEN_VERTICALLY_TRAVELLING -> if (direction.isVerticallyTravelling()) {
                queue.add(currentPosition.travel(TO_LEFT) to TO_LEFT)
                queue.add(currentPosition.travel(TO_RIGHT) to TO_RIGHT)
            } else {
                queue.add(currentPosition.travel(direction) to direction)
            }

            DIRECTING_SPLITTER_A -> queue.add(currentPosition.travel(direction.splitterA()) to direction.splitterA())
            DIRECTING_SPLITTER_B -> queue.add(currentPosition.travel(direction.splitterB()) to direction.splitterB())

            else -> error("Unknown object")
        }
    }

    fun findNumberOfEnergizedTiles(field: Field, startingPosition: Position, startingDirection: TravelingDirection): Int {
        val queue = LinkedList<Pair<Position, TravelingDirection>>()
        val cache = mutableSetOf<Pair<Position, TravelingDirection>>()

        val allVisitedPositions = mutableSetOf<Position>()
        queue.add(startingPosition to startingDirection)

        while (queue.isNotEmpty()) {
            val (position, direction) = queue.pop()
            field.shootBeam(queue, cache, position, direction, allVisitedPositions)
        }

        return allVisitedPositions.size
    }


    fun part1(input: List<String>): Int {
        val field = inputToField(input)
        return findNumberOfEnergizedTiles(field, Position(column = 0, row = 0), TO_RIGHT,)
    }

    fun part2(input: List<String>): Int {
        val field = inputToField(input)

        val startsToTry: MutableList<Pair<Position, TravelingDirection>> = mutableListOf()

        val maxRow = field.size - 1
        val maxColumn = field[0].size - 1

        field.indices.forEach { row ->
            startsToTry.add(Position(column = 0, row = row) to TO_RIGHT)
            startsToTry.add(Position(column = maxColumn, row = row) to TO_LEFT)
        }

        field[0].indices.forEach { column ->
            startsToTry.add(Position(column = column, row = 0) to DOWNWARDS)
            startsToTry.add(Position(column = column, row = maxRow) to UPWARDS)
        }

        return startsToTry.maxOf { start ->
            findNumberOfEnergizedTiles(field, start.first, start.second)
        }
    }

    testFile(
            "Part 1 Test 1",
            "Day16_test",
            ::part1,
            46
    )

    val input = readInput("Day16")
    measure { part1(input) }
            .also { check(it == 7884) }
            .println()

    testFile(
            "Part 2 Test 1",
            "Day16_test",
            ::part2,
            51
    )
    measure { part2(input) }
            .also { check(it >= 7884) }
            .println()
}