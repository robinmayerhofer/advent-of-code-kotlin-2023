import java.math.BigInteger
import java.security.MessageDigest
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.time.TimeSource
import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

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
    println("Duration :${result.duration}")
    return result.value
}
