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

    fun part1(input: List<String>): Long {
        val seeds = numberRegex.findAll(input[0].split(":")[1]).map { it.groups[0]!!.value.toLong() }.toList()
        val maps = mutableListOf<XtoYMap>()

        for (line in input.drop(1)) {
            if (line.isBlank()) continue

            mapNameRegex.find(line)?.groups?.lastOrNull()?.let { group ->
                maps.add(
                    XtoYMap(
                        name = group.value
                    )
                )
            }

            rangesRegex.findAll(line).toList()
                .takeIf { it.firstOrNull()?.groups?.size == 4 }
                ?.let { matchResults ->
                    val matchResult = matchResults[0]
                    val destinationRangeStart = matchResult.groups[1]!!.value.toLong()
                    val sourceRangeStart = matchResult.groups[2]!!.value.toLong()
                    val length = matchResult.groups[3]!!.value.toLong()

                    maps.last().rangeMappings.add(
                        RangeMapping(
                            sourceRange = LongRange(sourceRangeStart, sourceRangeStart + length - 1),
                            destinationRange = LongRange(destinationRangeStart, destinationRangeStart + length - 1),
                        )
                    )
                }
        }

        println(maps)

        return seeds.minOf { seed ->
            var value = seed
            for (map in maps) {
                value = map.mapValue(value)
            }
            value
        }
    }

    fun part2(input: List<String>): Long {
        return 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 35L
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day05")
    part1(input).println()

    val testInput2 = readInput("Day05_test2")
    val testOutput2 = part2(testInput2)
    val expectedTestOutput2 = 1L
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }
    part2(input).println()
}
