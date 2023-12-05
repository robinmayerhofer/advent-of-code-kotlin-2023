import kotlin.streams.asStream

fun main() {

    val numberRegex = "(\\d+)".toRegex()
    val mapNameRegex = "([a-z]+-to-[a-z]+) map:".toRegex()
    val rangesRegex = "(\\d+) (\\d+) (\\d+)".toRegex()

    data class RangeMapping(
        val sourceRange: LongRange,
        val destinationRange: LongRange,
    )

    data class XtoYMap(
        val name: String,
        val rangeMappings: MutableList<RangeMapping> = mutableListOf(),
    ) {
        fun mapValue(value: Long): Long =
            rangeMappings.firstNotNullOfOrNull {
                if (it.sourceRange.contains(value)) {
                    it.destinationRange.first + value - it.sourceRange.first
                } else {
                    null
                }
            } ?: value
    }

    fun String.extractMap(): XtoYMap? = mapNameRegex.find(this)?.groups?.lastOrNull()?.let { group ->
        XtoYMap(
            name = group.value
        )
    }

    fun String.extractRange(): RangeMapping? = rangesRegex.findAll(this).toList()
        .takeIf { it.firstOrNull()?.groups?.size == 4 }
        ?.let { matchResults ->
            val matchResult = matchResults[0]
            val destinationRangeStart = matchResult.groups[1]!!.value.toLong()
            val sourceRangeStart = matchResult.groups[2]!!.value.toLong()
            val length = matchResult.groups[3]!!.value.toLong()

            RangeMapping(
                sourceRange = LongRange(sourceRangeStart, sourceRangeStart + length - 1),
                destinationRange = LongRange(destinationRangeStart, destinationRangeStart + length - 1),
            )
        }

    fun List<String>.findAllMaps(): List<XtoYMap> {
        val input = this
        val maps = mutableListOf<XtoYMap>()

        for (line in input.drop(1)) {
            if (line.isBlank()) continue
            line.extractMap()?.let(maps::add)
            line.extractRange()?.let { maps.last().rangeMappings.add(it) }
        }

        return maps
    }

    fun Long.traverseMaps(maps: List<XtoYMap>): Long {
        var value = this
        for (map in maps) {
            value = map.mapValue(value)
        }
        return value
    }

    fun part1(input: List<String>): Long {
        val seeds = numberRegex.findAll(input[0].split(":")[1]).map { it.groups[0]!!.value.toLong() }.toList()
        val maps = input.findAllMaps()

        return seeds.minOf { seed ->
            seed.traverseMaps(maps)
        }
    }

    fun part2(input: List<String>): Long {
        var seedNumbers = numberRegex.findAll(input[0].split(":")[1]).map { it.groups[0]!!.value.toLong() }.toList()
        val seeds = mutableListOf<LongRange>()
        while (seedNumbers.isNotEmpty()) {
            seeds.add(
                seedNumbers[0]..< seedNumbers[0] + seedNumbers[1]
            )
            seedNumbers = seedNumbers.drop(2)
        }

        val seedCount = seeds.sumOf { it.count() }
        println("Seed count: $seedCount")

        val maps = input.findAllMaps()

        return seeds.asSequence().flatMap { it.asSequence() }
            .asStream().parallel()
            .map { seed ->
                seed.traverseMaps(maps)
            }.min(Long::compareTo).get()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 35L
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day05")
    measure { part1(input) }.println()

    val testOutput2 = part2(testInput)
    val expectedTestOutput2 = 46L
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }
    println("Test Part 2 passed")
    val input2 = readInput("Day05_2")
    measure { part2(input2) }.println()
}
