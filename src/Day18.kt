import kotlin.math.abs
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

        val minRow = min(a.row, b.row)
        val maxRow = max(a.row, b.row)
        val minColumn = min(a.column, b.column)
        val maxColumn = min(a.column, b.column)

        val isHorizontal: Boolean by lazy {
            a.row == b.row
        }

        val isVertical: Boolean by lazy {
            a.column == b.column
        }

        val length: Long by lazy {
            abs(a.column - b.column).toLong() + abs(a.row - b.row).toLong() + 1
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
                (min(a.row, b.row)..max(a.row, b.row)).map { r ->
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
            val minColumnDiff = minColumn - other.minColumn

            if (minColumnDiff != 0) {
                return minColumnDiff
            }
            return if (isVertical) {
                -1
            } else {
                1
            }
        }

    }


    fun dirFromChar(char: Char): Dir = when (char) {
        'R' -> Dir.EAST
        'U' -> Dir.NORTH
        'L' -> Dir.WEST
        'D' -> Dir.SOUTH
        else -> error("Unknown instruction. $char")
    }

    fun dirFromInt(int: Int): Dir = when (int) {
        0 -> Dir.EAST
        1 -> Dir.SOUTH
        2 -> Dir.WEST
        3 -> Dir.NORTH
        else -> error("Unknown instruction. $int")
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
                        val shouldIncrease = relevantEdges.filter { it.coversColumn(c) }
                            .any { it.countForHorizontalRayShootingForEvenOdd(r) }
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

    fun evenOddWholeGridButBetter(edges: Set<Edge>, vertices: Set<Vertex>): Long {
        val maxRow = vertices.maxOf { it.row }

        var insideCount = 0L

        // We are brute forcing the rows because the instance size is small enough.
        // We could also be smarter about it and maybe skip to the next row with a vertex and store the ranges that
        // are inside/outside and calculate the area of these rectangles?
        // This works and is simpler :)
        for (r in 0..maxRow) {
//            if (r % max(1, (maxRow / 1000)) == 0) {
//                println("0.1%!")
//            }

            var evenOddCount = 0
            val relevantEdges = edges
                .filter { it.coversRow(r) }
                .sorted() // sprt by minColumn and vertical before horizontal

            for (index in relevantEdges.indices) {
                if (relevantEdges[index].countForHorizontalRayShootingForEvenOdd(r)) {
                    evenOddCount += 1
                }

                if (relevantEdges[index].isVertical) {
                    insideCount += 1
                } else if (relevantEdges[index].isHorizontal) {
                    insideCount += relevantEdges[index].length - 2  // remove 2 because both ends are counted by a vertical line
                }

                if (index < relevantEdges.size - 1) {
                    val thisEdge = relevantEdges[index]
                    val nextEdge = relevantEdges[index + 1]
                    if (thisEdge.isVertical && nextEdge.isVertical) {
                        if (evenOddCount % 2 == 1) {
                            insideCount += (nextEdge.a.column - thisEdge.a.column - 1)
                        }
                    }
                }
            }
        }

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
        val vertices = buildVertexSet(edges)

        return evenOddWholeGridButBetter(edges, vertices).toInt()
    }

    fun part2(input: List<String>): Long {
        val instructions: List<Pair<Dir, Int>> = input.map { line ->
            val x = line.split("(")[1].drop(1).dropLast(1)
            val dir = x.last().digitToInt()
            val length = x.dropLast(1).toInt(radix = 16)

            dirFromInt(dir) to length
        }
        val edges = shiftOriginToZeroZero(buildEdgeSet(instructions))
        println(edges)
        val vertices = buildVertexSet(edges)

        return evenOddWholeGridButBetter(edges, vertices)
    }

    testFile(
        "Part 1 Test 1",
        "Day18_test",
        ::part1,
        62
    )
    println("Passed Test")

    val input = readInput("Day18").filter(String::isNotBlank)
    measure { part1(input) }
        .also { check(it == 58550) { "Got $it, expected 58550" } }
        .println()

    testFile(
        "Part 2 Test 1",
        "Day18_test",
        ::part2,
        952_408_144_115L
    )
    val input2 = readInput("Day18").filter(String::isNotBlank)
    measure { part2(input2) }
        .println()
}