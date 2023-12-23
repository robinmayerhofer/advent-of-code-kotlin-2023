import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Edge(
    val a: Vertex,
    val b: Vertex,
    val customLength: Int? = null,
) : Comparable<Edge> {

    val minRow by lazy { min(a.row, b.row) }
    val maxRow by lazy { max(a.row, b.row) }
    val minColumn by lazy { min(a.column, b.column) }
    val maxColumn by lazy { max(a.column, b.column) }

    val isHorizontal: Boolean by lazy {
        a.row == b.row
    }

    val isVertical: Boolean by lazy {
        a.column == b.column
    }

    val length: Int by lazy {
        customLength
            ?: (abs(a.column - b.column) + abs(a.row - b.row))
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

    // sort by minColumn then vertical edges before others
    override fun compareTo(other: Edge): Int {
        val minColumnDiff = minColumn - other.minColumn

        if (minColumnDiff != 0) {
            return minColumnDiff
        }
        return if (isVertical && other.isVertical) {
            minRow - other.minRow
        } else if (isVertical) {
            -1
        } else if (other.isVertical) {
            +1
        } else {
            minRow - other.minRow
        }
    }
}

typealias Vertex = Position

class Polygon(
    val edges: List<Edge>,
    val vertices: List<Vertex>
) {

    init {
        check(isPolygon()) { "That ain't a polygon." }
    }

    constructor(edges: List<Edge>) : this(edges, buildVertexSet(edges))


    companion object {
        fun buildVertexSet(edges: List<Edge>): List<Vertex> =
            edges.map { it.a }
    }

    /**
     * Complexity: O(E)
     */
    private fun isPolygon(): Boolean {
        if (edges.size < 3) {
            return false
        }
        if (edges.first().a != edges.last().b) {
            return false
        }

        return edges.zip(edges.drop(1)).all { (current, next) ->
            current.b == next.a
        }
    }

    /**
     * Whether the polygon is self-intersecting or not.
     * Complexity: O(V log V)
     */
    val isSimple: Boolean by lazy {
        TODO("Implement the Bentley Ottmann sweep line algorithm")
    }

    val isConvex: Boolean by lazy {
        isSimple && TODO("Implement this by checking the points (if 3 points in a row ")
    }

    /**
     * Complexity: O(E)
     */
    val enclosedArea: Long by lazy {
        if (isSimple) {
            enclosedAreaShoelace + perimeter / 2 + 1
        } else {
            TODO("Implement this with a sweep-line algorithm and the even-odd rule")
        }
    }

    private val enclosedAreaShoelace: Long by lazy {
        TODO("Calculate the enclosed area with the shoelace formula")
    }

    private val enclosedAreaEvenOddRule: Long by lazy {
        TODO("Calculate the enclosed area with the even odd rule")
    }

    /**
     * Complexity: O(E)
     */
    /**
     * Complexity: O(E)
     */
    val perimeter: Int by lazy {
        edges.sumOf { it.length } - 1
    }
}