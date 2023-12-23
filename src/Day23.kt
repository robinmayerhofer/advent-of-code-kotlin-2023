fun main() {
    shouldLog = true

    val PATH = '.'
    val FOREST = '#'
    val slopeToDirection = mapOf(
        '^' to Direction.NORTH,
        '>' to Direction.EAST,
        'v' to Direction.SOUTH,
        '<' to Direction.WEST,
    )

    fun longestPath(
        field: Field,
        alreadyVisited: Set<Position>,
        current: Position,
        end: Position,
    ): Int? {
        return Direction.entries.mapNotNull { direction ->
            val new = current.travel(direction)

            if (current == end) {
                0
            } else if (!field.isValidPosition(current)) {
                null
            } else if (new in alreadyVisited) {
                null
            } else {
                val element = field[current]
                if (element == FOREST) {
                    null
                } else if (element == PATH || slopeToDirection[element] == direction) {
                    val longestPath = longestPath(
                        field = field,
                        alreadyVisited = alreadyVisited + current,
                        current = new,
                        end = end,
                    )
                    if (longestPath == null) {
                        null
                    } else {
                        1 + longestPath
                    }
                } else {
                    null
                }
            }
        }.maxOrNull()
    }

    fun part1(input: List<String>): Int {
        val field = inputToField(input)
        val start = Position(row = 0, column = 1)
        val end = Position(row = field.size - 1, column = field[0].size - 2)
        return longestPath(
            field = field,
            alreadyVisited = emptySet(),
            current = start,
            end = end,
        )!!
    }

    fun longestPathGraph(
        graph: Map<Position, List<Edge>>,
        alreadyVisited: MutableMap<Position, Boolean>,
        current: Position,
        end: Position,
    ): Int? {
        return if (current == end) {
            0
        } else if (alreadyVisited[current] == true) {
            null
        } else {
            val edges: List<Edge> = graph[current]!!
            edges.mapNotNull { edge ->
                require(current == edge.a)
                alreadyVisited[edge.a] = true
                val pathLength = longestPathGraph(
                    graph = graph,
                    alreadyVisited = alreadyVisited,
                    current = edge.b,
                    end = end
                )
                alreadyVisited[edge.a] = false

                if (pathLength == null) {
                    null
                } else {
                    pathLength + edge.customLength!!
                }
            }.maxOrNull()
        }
    }

    fun pruneEdges(
        edges: Set<Edge>,
        start: Position,
        end: Position,
    ): Map<Position, List<Edge>> {

        val vertexToEdges: MutableMap<Position, MutableList<Edge>> = edges.fold(mutableMapOf()) { acc, edge ->
            acc.putIfAbsent(edge.a, mutableListOf())
            acc[edge.a]!!.add(edge)
            acc
        }

        while (true) {
            val (prunableVertex, prunableEdges) = vertexToEdges.entries
                .firstOrNull { it.value.size == 2 }
                ?: break

            require(prunableVertex !in setOf(start, end))

            val other = prunableEdges[0].b
            val other2 = prunableEdges[1].b

            val totalLength = prunableEdges[0].customLength!! + prunableEdges[1].customLength!!

            vertexToEdges.remove(prunableVertex)
            require(
                vertexToEdges[other]!!.remove(Edge(a = other, b = prunableVertex, customLength = prunableEdges[0].customLength))
            )
            require(
                vertexToEdges[other2]!!.remove(Edge(a = other2, b = prunableVertex, customLength = prunableEdges[1].customLength))
            )

            vertexToEdges[other]!!.add(Edge(a = other, b = other2, customLength = totalLength))
            vertexToEdges[other2]!!.add(Edge(a = other2, b = other, customLength = totalLength))
        }

        return vertexToEdges
    }

    fun part2(input: List<String>): Int {
        val field = inputToField(input)
            .map { row ->
                row.map {
                    if (it in slopeToDirection.keys) {
                        PATH
                    } else {
                        it
                    }
                }.toCharArray()
            }.toTypedArray()
        val start = Position(row = 0, column = 1)
        val end = Position(row = field.size - 1, column = field[0].size - 2)

        val edges: Set<Edge> = field.flatMapIndexed { row: Int, line: CharArray ->
            line.flatMapIndexed { column, char ->
                val position = Position(row = row, column = column)

                Direction.entries.map { direction ->
                    Edge(a = position, b = position.travel(direction), customLength = 1)
                }.filter { edge ->
                    field.isValidPosition(edge.a) && field.isValidPosition(edge.b) && field[edge.a] == PATH && field[edge.b] == PATH
                }
            }
        }.toSet()

        println("Edges (${edges.size})")
        log { edges }

        val verticesToEdges: Map<Position, List<Edge>> = pruneEdges(
            edges = edges,
            start = start,
            end = end
        )
        val prunedEdges = verticesToEdges.values.flatten()
        println("Pruned Edges (${prunedEdges.size})")
        log { prunedEdges.joinToString("\n") }

        require(start in verticesToEdges)
        require(end in verticesToEdges)


        return longestPathGraph(
            graph = verticesToEdges,
            alreadyVisited = mutableMapOf(),
            current = start,
            end = end,
        )!!
    }

    testFile(
        "Part 1 Test 1",
        "Day23_test",
        ::part1,
        94,
        filterBlank = false,
    )

    val input = readInput("Day23").filter(String::isNotBlank)
    measure { part1(input) }
        .also { check(it == 2170) }
        .println()

    shouldLog = false
    testFile(
        "Part 2 Test 1",
        "Day23_test",
        ::part2,
        154,
        filterBlank = false,
    )
    shouldLog = false
    val input2 = readInput("Day23").filter(String::isNotBlank)
    measure { part2(input2) }.println()
}