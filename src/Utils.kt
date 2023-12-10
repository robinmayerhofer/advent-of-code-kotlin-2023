import java.math.BigInteger
import java.security.MessageDigest
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
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun inputToField(input: List<String>): Array<CharArray> {
    val rows = input.size
    val columns = input.firstOrNull()?.length ?: 0

    val field = Array(rows) { CharArray(columns) }

    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, character ->
            field[row][column] = character
        }
    }
    return field
}

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
