import kotlin.math.max
import kotlin.math.min

private const val ACCEPT = "A"
private const val REJECT = "R"

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
    val next: WorkflowName
    fun matches(part: Part): Boolean
    fun process(part: Part): WorkflowName

    fun acceptedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange>
    fun rejectedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange>
}

private data class ElseRule(override val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        true

    override fun process(part: Part) =
        next

    override fun acceptedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        ranges

    override fun rejectedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        emptyMap()
}

private data class LessThanRule(val char: Char, val lessThan: Int, override val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        (part.charToValue[char] ?: error("Did not find $char in part $part")) < lessThan

    override fun process(part: Part) =
        next

    override fun acceptedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        ranges.mapValues { (rangeChar, range) ->
            if (rangeChar != char) {
                range
            } else {
                range.first until min(range.last + 1, lessThan)
            }
        }

    override fun rejectedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        ranges.mapValues { (rangeChar, range) ->
            if (rangeChar != char) {
                range
            } else {
                max(range.first, lessThan)..range.last
            }
        }
}

private data class GreaterThanRule(val char: Char, val greaterThan: Int, override val next: WorkflowName) : WorkflowRule {
    override fun matches(part: Part) =
        (part.charToValue[char] ?: error("Did not find $char in part $part")) > greaterThan

    override fun process(part: Part) =
        next

    override fun acceptedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        ranges.mapValues { (rangeChar, range) ->
            if (rangeChar != char) {
                range
            } else {
                max(range.first, greaterThan + 1)..range.last
            }
        }

    override fun rejectedRanges(ranges: Map<Char, IntRange>): Map<Char, IntRange> =
        ranges.mapValues { (rangeChar, range) ->
            if (rangeChar != char) {
                range
            } else {
                range.first .. min(range.last, greaterThan)
            }
        }
}

// "a<2006:qkq"
private val workflowRegex = "([xmas])([<>])(\\d+):([a-zA-Z]+)".toRegex()

fun main() {

    fun Map<Char, IntRange>.hasNoSolutions(): Boolean =
        any { it.value.isEmpty() }

    fun IntRange.count(): Int =
        if (isEmpty()) {
            0
        } else {
            last - first + 1
        }

    fun Map<Char, IntRange>.solutions(): Long =
        map { it.value }
            .fold(initial = 1L) { value: Long, range: IntRange ->
                value * (range.count())
            }

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

        log { "Workflows" }
        log { workflows.entries.joinToString("\n") }
        log()
        log("Parts")
        log { parts.joinToString("\n") }

        return parts
            .filter { part ->
                var currentWorkflow = "in"

                val terminalStates = setOf(ACCEPT, REJECT)

                while (currentWorkflow !in terminalStates) {
                    currentWorkflow = workflows[currentWorkflow]!!.process(part)

                }

                when (currentWorkflow) {
                    ACCEPT -> true
                    REJECT -> false
                    else -> error(":(")
                }
            }
            .sumOf { it.charToValue.values.sum() }
    }

    fun Int.toDashes(): String = (0..this).joinToString("") { "-" }

    fun solutions(
        depth: Int,
        workflows: Map<WorkflowName, Workflow>,
        currentName: WorkflowName,
        ranges: Map<Char, IntRange>,
    ): Long {
        // This is some sort of "hypercube splitting"
        if (ranges.hasNoSolutions()) {
            log { "${depth.toDashes()} Considering $currentName: ranges are empty" }
            return 0
        }
        if (currentName == REJECT) {
            log { "${depth.toDashes()} ❌" }
            return 0
        }
        if (currentName == ACCEPT) {
            val result = ranges.solutions()
            log { "${depth.toDashes()} ✅ $ranges ($result)" }
            return result
        }

        val current = workflows[currentName]!!
        log { "${depth.toDashes()} $ranges => Considering $current" }

        var currentRanges = ranges
        var solutionCount = 0L
        for (rule in current.rules) {
            solutionCount += solutions(depth + 1, workflows, rule.next, rule.acceptedRanges(currentRanges))
            currentRanges = rule.rejectedRanges(currentRanges)
        }

        return solutionCount
    }

    fun part2(input: List<String>): Long {
        val workflows = input
            .takeWhile { it.isNotEmpty() }
            .map { it.lineToWorkflow() }
            .associateBy { it.name }

        val start = "in"

        val fullRange = mapOf(
            'x' to 1..4000,
            'm' to 1..4000,
            'a' to 1..4000,
            's' to 1..4000,
        )

        return solutions(0, workflows, start, fullRange)
    }

    val greaterThanRule = GreaterThanRule(
        char = 'a',
        greaterThan = 1000,
        next = "A",
    )
    check(1001..4000 == greaterThanRule.acceptedRanges(mapOf('a' to 1..4000))['a'])
    check(1..1000 == greaterThanRule.rejectedRanges(mapOf('a' to 1..4000))['a'])

    val lessThanRule = LessThanRule(
        char = 'a',
        lessThan = 1000,
        next = "A",
    )
    check(1..999 == lessThanRule.acceptedRanges(mapOf('a' to 1..4000))['a'])
    check(1000..4000 == lessThanRule.rejectedRanges(mapOf('a' to 1..4000))['a'])

    println("Test Part 1")
    testFile(
        "Part 1 Test 1",
        "Day19_test",
        ::part1,
        19114,
        filterBlank = false,
    )

    println("Run Part 1")
    val input = readInput("Day19")
    measure { part1(input) }
        .println()

    println("Test Part 2")
    testFile(
        "Part 2 Test 1",
        "Day19_test",
        ::part2,
        167409079868000L,
        filterBlank = false,
    )
    println("Run Part 2")
    val input2 = readInput("Day19").filter(String::isNotBlank)
    measure { part2(input2) }
        .println()
}
