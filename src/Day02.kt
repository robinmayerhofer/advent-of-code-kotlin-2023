import Color.*

enum class Color {
    blue,
    green,
    red
}


fun main() {
    fun part1(input: List<String>): Int =
            input.map { line ->
                val limit = mapOf(
                        red to 12,
                        green to 13,
                        blue to 14,
                )

                val maxValues = mutableMapOf<Color, Int>()

                val (game, sets) = line.split(":")

                val gameNumber = "Game (\\d+)".toRegex().find(game)!!.groupValues[1].toInt()

                sets.split(";").forEach { set ->
                    println("Set: $set")
                    val green = "(\\d+) green".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
                    val red = "(\\d+) red".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
                    val blue = "(\\d+) blue".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0

                    maxValues.merge(Color.red, red, ::maxOf)
                    maxValues.merge(Color.blue, blue, ::maxOf)
                    maxValues.merge(Color.green, green, ::maxOf)
                }

                for (limitEntry in limit) {
                    if (limitEntry.value < (maxValues[limitEntry.key] ?: 0)) {
                        println("Game is NOT possible: $gameNumber")
                        return@map 0
                    }
                }
                println("Game is possible: $gameNumber, $maxValues")
                return@map gameNumber
            }.sum()

    fun part2(input: List<String>): Int =
            input.map { line ->

                val maxValues = mutableMapOf<Color, Int>()

                val (game, sets) = line.split(":")

                val gameNumber = "Game (\\d+)".toRegex().find(game)!!.groupValues[1].toInt()

                sets.split(";").forEach { set ->
                    println("Set: $set")
                    val green = "(\\d+) green".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
                    val red = "(\\d+) red".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0
                    val blue = "(\\d+) blue".toRegex().find(set)?.groupValues?.lastOrNull()?.toInt() ?: 0

                    maxValues.merge(Color.red, red, ::maxOf)
                    maxValues.merge(Color.blue, blue, ::maxOf)
                    maxValues.merge(Color.green, green, ::maxOf)
                }

                maxValues.values.reduce { a, b -> a * b }
            }.sum()


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 8)

    val testInput2 = readInput("Day02_test")
    check(part2(testInput2) == 2286)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
