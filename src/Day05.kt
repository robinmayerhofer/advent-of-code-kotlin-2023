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

        return seeds.minOf { seed ->
            var value = seed
            for (map in maps) {
                value = map.mapValue(value)
            }
            value
        }
    }

    fun part2(input: List<String>): Long {
        var seedNumbers = numberRegex.findAll(input[0].split(":")[1]).map { it.groups[0]!!.value.toLong() }.toList()
        val seeds = mutableListOf<LongRange>()
        while (seedNumbers.isNotEmpty()) {
            seeds.add(
                seedNumbers[0]..<seedNumbers[0] + seedNumbers[1]
            )
            seedNumbers = seedNumbers.drop(2)
        }

        val seedCount = seeds.sumOf { it.count() }
        println("Seed count: $seedCount")

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

        return seeds.asSequence().flatMap { it.asSequence() }.mapIndexed { index, seed ->
            if (index % 10_000 == 0) {
                println("Progress: ${(index.toDouble()/seedCount.toDouble() * 100)}%")
            }
            var value = seed
            for (map in maps) {
                value = map.mapValue(value)
            }
            value
        }.min()
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

    val testOutput2 = part2(testInput)
    val expectedTestOutput2 = 46L
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }
    println("Test Part 2 passed")
    val input2 = readInput("Day05_2")
    part2(input2).println()
}
