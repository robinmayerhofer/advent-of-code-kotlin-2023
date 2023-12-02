import Color.*

enum class Color {
    BLUE,
    GREEN,
    RED,
}

private val gameRegex = "Game (\\d+)".toRegex()
private val greenRegex = "(\\d+) green".toRegex()
private val redRegex = "(\\d+) red".toRegex()
private val blueRegex = "(\\d+) blue".toRegex()

fun main() {
    fun findGameNumber(line: String): Int =
        gameRegex.find(line)!!.groupValues[1].toInt()

    fun adaptMaxFromSet(maxValues: MutableMap<Color, Int>, set: String) {
        val green = greenRegex.find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
        val red = redRegex.find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
        val blue = blueRegex.find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0

        maxValues.merge(RED, red, ::maxOf)
        maxValues.merge(BLUE, blue, ::maxOf)
        maxValues.merge(GREEN, green, ::maxOf)
    }

    fun findMaxColorValuesInLine(line: String): Map<Color, Int> {
        val maxValues = mutableMapOf<Color, Int>()
        val (game, sets) = line.split(":")

        sets.split(";").forEach { set ->
            adaptMaxFromSet(maxValues, set)
        }

        return maxValues
    }

    fun part1(input: List<String>): Int =
            input.sumOf { line ->
                val limit = mapOf(
                        RED to 12,
                        GREEN to 13,
                        BLUE to 14,
                )

                val gameNumber = findGameNumber(line)
                val maxValues = findMaxColorValuesInLine(line)

                gameNumber.takeIf {
                    limit.all {  limitEntry ->
                        limitEntry.value >= (maxValues[limitEntry.key] ?: 0)
                    }
                } ?: 0
            }

    fun part2(input: List<String>): Int =
            input.sumOf { line ->
                val maxValues = findMaxColorValuesInLine(line)
                maxValues.values.reduce { a, b -> a * b }
            }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val testInput2 = readInput("Day02_test")
    check(part2(testInput2) == 2286)

    val input = readInput("Day02")

    part1(input).println()
    part2(input).println()
}
