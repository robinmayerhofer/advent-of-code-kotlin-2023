
import kotlin.math.max
import kotlin.math.min

typealias Vertex = Position
typealias Day18Input = List<Pair<Dir, Int>>

private const val LINE = '#'
private const val INSIDE = '0'
private const val NOTHING = '.'

fun main() {


    data class Edge(
        val a: Vertex,
        val b: Vertex,
    ) : Comparable<Edge> {

        val isHorizontal: Boolean by lazy {
            a.row == b.row
        }

        val isVertical: Boolean by lazy {
            a.column == b.column
        }

        fun coversRow(row: Int): Boolean =
            (a.row <= row && b.row >= row) || (b.row <= row && a.row >= row)

        fun coversColumn(column: Int): Boolean =
            (a.column <= column && b.column >= column) || (b.column <= column && a.column >= column)

        fun coveredVertices(): Iterable<Vertex> =
            if (isHorizontal) {
                (min(a.column, b.column)..max(a.column, b.column)).map { c ->
                    Vertex(column = c, row = a.row)
                }
            } else if (isVertical) {
                (min(a.row, b.row) .. max(a.row, b.row)).map { r ->
                    Vertex(column = a.column, row = r)
                }
            } else {
                error("Invalid edge")
            }

        fun countForHorizontalRayShootingForEvenOdd(row: Int): Boolean {
            if (!isVertical) {
                return false
            }

            if (!coversRow(row)) {
                return false
            }

            // is on top
            return a.row < row || b.row < row
        }

        override fun compareTo(other: Edge): Int {
            val rowDiff = a.row - b.row

            if (rowDiff != 0) {
                return rowDiff
            }

            return a.column - b.column
        }
    }

    fun dirFromChar(char: Char): Dir = when (char) {
        'R' -> Dir.EAST
        'U' -> Dir.NORTH
        'L' -> Dir.WEST
        'D' -> Dir.SOUTH
        else -> error("Unknown instruction.")
    }

    fun buildEdgeSet(input: Day18Input): Set<Edge> {
        var current = Vertex(0, 0)
        val edges = mutableSetOf<Edge>()

        for ((direction, steps) in input) {
            val next = current.travel(direction, steps)
            edges.add(
                Edge(
                    a = current,
                    b = next
                )
            )
            current = next
        }
        return edges
    }

    fun buildVertexSet(edges: Set<Edge>): Set<Vertex> =
        edges.flatMap { listOf(it.a, it.b) }
            .toSet()

    fun shiftOriginToZeroZero(edges: Set<Edge>): Set<Edge> {
        val vertices = buildVertexSet(edges)

        val minColumn = vertices.minOf { it.column }
        val minRow = vertices.minOf { it.row }

        val addToColumns = 0 - minColumn
        val addToRows = 0 - minRow

        return edges.map { edge ->
            Edge(
                a = edge.a.copy(
                    column = edge.a.column + addToColumns,
                    row = edge.a.row + addToRows
                ),
                b = edge.b.copy(
                    column = edge.b.column + addToColumns,
                    row = edge.b.row + addToRows
                )
            )
        }.toSet()
    }

    fun evenOddWholeGrid(edges: Set<Edge>, vertices: Set<Vertex>): Int {
        val minColumn = vertices.minOf { it.column }
        val minRow = vertices.minOf { it.row }
        val maxColumn = vertices.maxOf { it.column }
        val maxRow = vertices.maxOf { it.row }

        val field = Field(maxRow + 1) {
            CharArray(maxColumn + 1) {
                NOTHING
            }
        }
        val fieldCopy = Field(maxRow + 1) {
            CharArray(maxColumn + 1) {
                NOTHING
            }
        }

        edges.flatMap { it.coveredVertices() }.forEach { vertex ->
            field[vertex.row][vertex.column] = LINE
        }

        var insideCount = 0

        for (r in 0..maxRow) {
            var evenOddCount = 0
            val relevantEdges = edges.filter { it.coversRow(r) }

            for (c in 0..maxColumn) {
                val position = Position(column = c, row = r)
                when (field.elementAt(position)) {
                    NOTHING -> {
                        if (evenOddCount % 2 == 1) {
                            insideCount += 1
                            fieldCopy[position.row][position.column] = INSIDE
                        }
                    }
                    LINE -> {
                        insideCount += 1
                        fieldCopy[position.row][position.column] = INSIDE
                        val shouldIncrease = relevantEdges.filter { it.coversColumn(c) }.any { it.countForHorizontalRayShootingForEvenOdd(r) }
                        if (shouldIncrease) {
                            evenOddCount += 1
                        }

                    }
                }
            }
        }

        fieldCopy.println()

        return insideCount
    }

    fun part1(input: List<String>): Int {
        val instructions: List<Pair<Dir, Int>> = input.map { line ->
            val dirAndLength = line.split("(")[0]
            val dir = dirAndLength[0]
            val length = dirAndLength.findAllNumbers()[0]

            dirFromChar(dir) to length
        }
        val edges = shiftOriginToZeroZero(buildEdgeSet(instructions))
        println(edges)
        val vertices = buildVertexSet(edges)


        return evenOddWholeGrid(edges, vertices)
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    testFile(
        "Part 1 Test 1",
        "Day18_test",
        ::part1,
        62
    )
    println("Passed Test")

    val input = readInput("Day18").filter(String::isNotBlank)
    part1(input).println()

//    testFile(
//        "Part 2 Test 1",
//        "Day18_test",
//        ::part2,
//        1
//    )
//    val input2 = readInput("Day18_2").filter(String::isNotBlank)
//    part2(input2).println()
}