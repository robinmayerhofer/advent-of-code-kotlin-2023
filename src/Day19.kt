fun main() {

    data class WorkflowSteps()

    data class Workflow(
        val name: String
        val steps: List<WorkflowSteps>
    ) {
        fun pro
    }

    fun String.toWorkflow() {

    }

    fun part1(input: List<String>): Int {
        val rawWorkflows = input.takeWhile { it.isNotEmpty() }
        val rawParts = input.drop(rawWorkflows.size).filter { it.isNotEmpty() }


    }
        input.sumOf {
            it.length
        }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    testFile(
        "Part 1 Test 1",
        "Day19_test",
        ::part1,
        19114
    )

//    val input = readInput("Day19").filter(String::isNotBlank)
//    part1(input).println()
//
//    testFile(
//        "Part 2 Test 1",
//        "Day19_test",
//        ::part2,
//        1
//    )
//    val input2 = readInput("Day19_2").filter(String::isNotBlank)
//    part2(input2).println()
}
