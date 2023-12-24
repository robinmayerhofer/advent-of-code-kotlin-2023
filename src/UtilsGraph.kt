import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class UndirectedEdge(
    val a: Vertex,
    val b: Vertex,
    val customLength: Int? = null,
) : Comparable<UndirectedEdge> {

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

    fun otherVertex(vertex: Vertex): Vertex {
        require(vertex == a || vertex == b)
        return if (vertex == a) {
            b
        } else {
            a
        }
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
    // TODO: make this work for arbitrary edges => extract into standalone comparator
    override fun compareTo(other: UndirectedEdge): Int {
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UndirectedEdge

        if (customLength != other.customLength) return false
        if (minRow != other.minRow) return false
        if (maxRow != other.maxRow) return false
        if (minColumn != other.minColumn) return false
        if (maxColumn != other.maxColumn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = customLength ?: 0
        result = 31 * result + minRow
        result = 31 * result + maxRow
        result = 31 * result + minColumn
        result = 31 * result + maxColumn
        return result
    }
}

data class Graph(
    val edges: Set<UndirectedEdge>,
    val vertices: Set<Vertex> = edges.flatMap { listOf(it.a, it.b) }.toSet(),
) {
    val vertexToEdges: MutableMap<Position, MutableList<UndirectedEdge>> by lazy {
        computeVertexToEdgesMap()
    }

    private fun computeVertexToEdgesMap(): MutableMap<Position, MutableList<UndirectedEdge>> =
        edges.fold(mutableMapOf()) { acc, edge ->
            acc.putIfAbsent(edge.a, mutableListOf())
            acc.putIfAbsent(edge.b, mutableListOf())
            acc[edge.a]!!.add(edge)
            acc[edge.b]!!.add(edge)
            acc
        }

    fun pruningEdges(): Graph {
        val vertexToEdges: MutableMap<Position, MutableList<UndirectedEdge>> = computeVertexToEdgesMap()

        while (true) {
            val (prunableVertex, prunableEdges) = vertexToEdges.entries
                .firstOrNull { it.value.size == 2 }
                ?: break

            val other: Vertex = prunableEdges[0].otherVertex(prunableVertex)
            val other2: Vertex = prunableEdges[1].otherVertex(prunableVertex)

            val totalLength = prunableEdges[0].length + prunableEdges[1].length
            val replacementEdge = UndirectedEdge(a = other, b = other2, customLength = totalLength)

            check(vertexToEdges.remove(prunableVertex) != null)
            require(vertexToEdges[other]!!.remove(prunableEdges[0]))
            require(vertexToEdges[other2]!!.remove(prunableEdges[1]))

            vertexToEdges[other]!!.add(replacementEdge)
            vertexToEdges[other2]!!.add(replacementEdge)
        }

        return Graph(edges = vertexToEdges.values.flatten().toSet(), vertices = vertexToEdges.keys)
    }

    /**
     * Brute-force. In general, longest path is an NP-hard problem.
     */
    fun longestPath(
        alreadyVisited: MutableMap<Position, Boolean> = mutableMapOf(),
        current: Position,
        end: Position,
    ): Int? {
        return if (current == end) {
            0
        } else if (alreadyVisited[current] == true) {
            null
        } else {
            val edges: List<UndirectedEdge> = vertexToEdges[current]!!
            edges.mapNotNull { edge ->
                require(current == edge.a || current == edge.b)
                alreadyVisited[current] = true
                val pathLength = longestPath(
                    alreadyVisited = alreadyVisited,
                    current = edge.otherVertex(current),
                    end = end
                )
                alreadyVisited[current] = false

                if (pathLength == null) {
                    null
                } else {
                    pathLength + edge.customLength!!
                }
            }.maxOrNull()
        }
    }

}

typealias Vertex = Position

data class Polygon(
    val edges: List<UndirectedEdge>,
    val vertices: List<Vertex>
) {

    init {
        check(isPolygon()) { "That ain't a polygon." }
    }

    constructor(edges: List<UndirectedEdge>) : this(edges, buildVertexSet(edges))


    companion object {
        fun buildVertexSet(edges: List<UndirectedEdge>): List<Vertex> =
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