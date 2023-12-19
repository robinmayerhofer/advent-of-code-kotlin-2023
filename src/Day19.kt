private enum class Operation {
    LESS_THAN,
    GREATER_THAN,
    ;
}


private data class Part(
    val charToValue: Map<Char, Int>,
) {
    companion object {
        val regex = "([xmas])=(\\d+)".toRegex()


        fun from(line: String): Part =
            Part(
                regex.findAll(line).map {
                    val char = it.groups[1]!!.value.first()
                    val value = it.groups[2]!!.value.toInt()

                    char to value
                }.toMap()
            )
    }
}

typealias WorkflowName = String

private interface WorkflowRule {
    fun matches(part: Part): Boolean
    fun process(part: Part): WorkflowName
}

private data class ElseRule(val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        true

    override fun process(part: Part) =
        next
}

private data class LessThanRule(val char: Char, val value: Int, val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        (part.charToValue[char] ?: error("Did not find $char in part $part")) < value

    override fun process(part: Part) =
        next
}

private data class GreaterThanRule(val char: Char, val value: Int, val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        (part.charToValue[char] ?: error("Did not find $char in part $part")) > value

    override fun process(part: Part) =
        next
}

// "a<2006:qkq"
private val workflowRegex = "([xmas])([<>])(\\d+):([a-zA-Z]+)".toRegex()

fun main() {

    data class Workflow(
        val name: WorkflowName,
        val rules: List<WorkflowRule>,

        ) {
        fun process(part: Part): String =
            rules
                .first { step -> step.matches(part = part) }
                .process(part)
    }

    fun String.toWorkflowRule(): WorkflowRule {
        val matchResult = workflowRegex.find(this)
        if (matchResult?.groups == null) {
            return ElseRule(this)
        }

        val groups = matchResult.groups
        val char = groups[1]!!.value.first()
        val comparator = groups[2]!!.value.first()
        val value = groups[3]!!.value.toInt()
        val nextWorkflowName = groups[4]!!.value

        return when (comparator) {
            '<' -> LessThanRule(char, value, nextWorkflowName)
            '>' -> GreaterThanRule(char, value, nextWorkflowName)
            else -> error("Unknown comparator '$comparator'.")
        }

    }

    fun String.lineToWorkflow(): Workflow {
        this.split("{")

        val (name, rest) = split("{")
        val rules = rest
            .dropLast(1)
            .split(",")
            .map { it.toWorkflowRule() }

        return Workflow(
            name = name,
            rules = rules
        )
    }

    fun part1(input: List<String>): Int {
        val workflows = input
            .takeWhile { it.isNotEmpty() }
            .map { it.lineToWorkflow() }
            .associateBy { it.name }

        val parts = input.drop(workflows.size)
            .filter { it.isNotEmpty() }
            .map(Part::from)

        println("Workflows")
        workflows.forEach(::println)
        println()
        println("Parts")
        parts.forEach(::println)

        return parts
            .filter { part ->
                var currentWorkflow = "in"
                val accept = "A"
                val reject = "R"
                val terminalStates = setOf(accept, reject)

                while (currentWorkflow !in terminalStates) {
                    currentWorkflow = workflows[currentWorkflow]!!.process(part)

                }

                when (currentWorkflow) {
                    accept -> true
                    reject -> false
                    else -> error(":(")
                }
            }
            .sumOf { it.charToValue.values.sum() }
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    testFile(
        "Part 1 Test 1",
        "Day19_test",
        ::part1,
        19114,
        filterBlank = false,
    )

    val input = readInput("Day19")
    part1(input).println()

//    testFile(
//        "Part 2 Test 1",
//        "Day19_test",
//        ::part2,
//        1
//    )
//    val input2 = readInput("Day19_2").filter(String::isNotBlank)
//    part2(input2).println()
}
