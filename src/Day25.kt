import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultWeightedEdge
import org.jgrapht.graph.SimpleWeightedGraph

fun main() {
    shouldLog = true

    fun part1(input: List<String>): Int {
        val graph = SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge::class.java)

        input.forEach { line ->
            val (name, others) = line.split(": ")

            graph.addVertex(name)

            others.split(" ").forEach { other ->
                graph.addVertex(other)
                graph.addEdge(name, other)
            }
        }

        val oneSide = StoerWagnerMinimumCut(graph).minCut()
        return (graph.vertexSet().size - oneSide.size) * oneSide.size
    }

    val input = readInput("Day25").filter(String::isNotBlank)
    measure { part1(input) }
        .println()
}
