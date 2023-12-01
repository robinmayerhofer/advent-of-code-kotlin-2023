fun main() {
    fun part1(input: List<String>): Int =
            input.sumOf { line ->
                val numbers = line.mapNotNull { it.digitToIntOrNull() }
                "${numbers.first()}${numbers.last()}".toInt()
            }


    fun part2(input: List<String>): Int {
        val stringToNumber = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
        )

        return input.sumOf { _line ->
            var first: Int? = null
            var last: Int? = null


            var line: String = _line
            for (i in line.indices) {
                var digit = line[0].digitToIntOrNull()
                if (digit == null) {
                    digit = stringToNumber.entries.firstNotNullOfOrNull { (numberString, number) ->
                        if (line.startsWith(numberString)) {
                            number
                        } else {
                            null
                        }
                    }
                }

                if (digit != null) {
                    if (first == null) {
                        first = digit
                    }
                    last = digit
                }

                line = line.drop(1)
            }

            "${first}${last}".toInt()
        }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 142)

    val testInput2 = readInput("Day01_test02")
    check(part2(testInput2) == 281)

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
