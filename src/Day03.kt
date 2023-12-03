
fun main() {

    fun part1(input: List<String>): Int =
            input.sumOf { 
                it.length
            }

    fun part2(input: List<String>): Int =
            input.sumOf {
                it.length
            }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)

    val input = readInput("Day03")
    part1(input).println()

     val testInput2 = readInput("Day03_test02")
     check(part2(testInput2) == 1)
    part2(input).println()
}
