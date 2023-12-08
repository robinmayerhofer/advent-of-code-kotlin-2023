fun main() {

    data class Node(
            val id: String,
            val left: String,
            val right: String,
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Node

            return id == other.id
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }

        val isStart by lazy { id == "AAA" }

        val isStart2 by lazy { id.endsWith("A") }

        val isEnd by lazy { id == "ZZZ" }

        val isEnd2 by lazy { id.endsWith("Z") }
    }

    fun parseInput(input: List<String>): Pair<List<Char>, Map<String, Node>> {
        val stepDirections = input[0].replace(" ", "").toList()
        val nodes: Map<String, Node> = input.drop(1).associate { node ->
            val (id, options) = node.split("=")
            val matches = "\\(([1-9A-Z]+), ([1-9A-Z]+)\\)".toRegex().findAll(options).toList()
            val left = matches[0].groups[1]!!.value
            val right = matches[0].groups[2]!!.value

            id.trim() to Node(id.trim(), left, right)
        }

        return Pair(stepDirections, nodes)
    }

    fun steps(stepDirections: List<Char>, nodes: Map<String, Node>, startNode: Node, isEnd: (Node) -> Boolean): Long {
        var currentNode = startNode
        var steps = 0L
        while (!isEnd(currentNode)) {
            for (stepDirection in stepDirections) {
                currentNode = when (stepDirection) {
                    'L' -> nodes[currentNode.left]!!
                    'R' -> nodes[currentNode.right]!!
                    else -> error("Invalid step direction")
                }
                steps += 1
            }
        }

        return steps
    }


    fun part1(input: List<String>): Long {
        val (stepDirections, nodes) = parseInput(input)
        val startNode = nodes.values.first { it.isStart }
        return steps(stepDirections, nodes, startNode, Node::isEnd)
    }

    fun part2(input: List<String>): Long {
        val (stepDirections, nodes) = parseInput(input)
        val startNodes = nodes.values.filter(Node::isStart2)

        return startNodes.map { startNode ->
            steps(stepDirections, nodes, startNode, Node::isEnd2)
        }.reduce(::lcm)

    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test").filter(String::isNotBlank)
    val testOutput = part1(testInput)
    val expectedTestOutput = 2L
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val testInput2 = readInput("Day08_test2").filter(String::isNotBlank)
    val testOutput2 = part1(testInput2)
    val expectedTestOutput2 = 6L
    check(testOutput2 == expectedTestOutput2) {
        "Part 1 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }

    val input = readInput("Day08").filter(String::isNotBlank)
    part1(input).println()

    val testInput3 = readInput("Day08_test3").filter(String::isNotBlank)
    val testOutput3 = part2(testInput3)
    val expectedTestOutput3 = 6L
    check(testOutput3 == expectedTestOutput3) {
        "Part 2 Tests: Expected $expectedTestOutput3, got $testOutput3"
    }

    // 16342438708751
    part2(input).println()
}