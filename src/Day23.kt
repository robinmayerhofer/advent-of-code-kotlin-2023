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
        alreadyVisited: MutableMap<Position, Boolean>,
        current: Position,
        end: Position,
    ): Int? {
        return Direction.entries.mapNotNull { direction ->
            val new = current.travel(direction)

            if (current == end) {
                0
            } else if (!field.isValidPosition(current)) {
                null
            } else if (alreadyVisited[new] == true) {
                null
            } else {
                val element = field[current]
                if (element == FOREST) {
                    null
                } else if (element == PATH || slopeToDirection[element] == direction) {
                    alreadyVisited[current] = true
                    val longestPath = longestPath(
                        field = field,
                        alreadyVisited = alreadyVisited,
                        current = new,
                        end = end,
                    )
                    alreadyVisited[current] = false
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
            alreadyVisited = mutableMapOf(),
            current = start,
            end = end,
        )!!
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

        val rawGraph: Graph = field.flatMapIndexed { row: Int, line: CharArray ->
            line.flatMapIndexed { column, char ->
                val position = Position(row = row, column = column)

                Direction.entries.map { direction ->
                    UndirectedEdge(a = position, b = position.travel(direction), customLength = 1)
                }.filter { edge ->
                    field.isValidPosition(edge.a) && field.isValidPosition(edge.b) && field[edge.a] == PATH && field[edge.b] == PATH
                }
            }
        }.let {
            Graph(edges = it.toSet())
        }

        println("Edges (${rawGraph.edges.size})")
        log { rawGraph.edges }

        val graph: Graph = rawGraph.pruningEdges()
        println("Pruned Edges (${graph.edges.size})")
        log { graph.edges.joinToString("\n") }

        require(start in graph.vertices)
        require(end in graph.vertices)

        return graph.longestPath(
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