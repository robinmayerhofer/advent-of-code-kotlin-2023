data class Position(val column: Int, val row: Int)

enum class Direction(val deltaX: Int, val deltaY: Int) {
    NORTH(deltaX = 0, deltaY = -1),
    SOUTH(deltaX = 0, deltaY = 1),
    EAST(deltaX = 1, deltaY = 0),
    WEST(deltaX = -1, deltaY = 0),
    ;

    fun reverse() = when (this) {
        NORTH -> SOUTH
        SOUTH -> NORTH
        EAST -> WEST
        WEST -> EAST
    }
}

fun Position.travel(direction: Direction, steps: Int = 1): Position =
    copy(column = column + direction.deltaX * steps, row = row + direction.deltaY * steps)
