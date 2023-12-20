import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.writeLines
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

fun writeOutput(name: String, lines: Iterable<CharSequence>) = Path("src/$name.txt").writeLines(lines)

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


inline fun <T> measure(block: () -> T): T {
    val result = measureTimedValue(block)
    println("Duration: ${result.duration}")
    return result.value
}

val numberRegex = "(-?\\d+)".toRegex()

fun String.findAllNumbers(): List<Int> =
    numberRegex.findAll(this).map { it.groups[0]!!.value.toInt() }.toList()

fun String.findAllNumbersLong(): List<Long> =
    numberRegex.findAll(this).map { it.groups[0]!!.value.toLong() }.toList()

fun gcd(a: Long, b: Long): Long {
    var a = a
    var b = b
    while (b > 0) {
        val temp = b
        b = a % b // % is remainder
        a = temp
    }
    return a
}

fun lcm(a: Long, b: Long): Long {
    return a * (b / gcd(a, b))
}
