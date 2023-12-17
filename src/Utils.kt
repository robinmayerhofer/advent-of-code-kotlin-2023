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

typealias Field = Array<CharArray>

fun inputToField(input: List<String>): Field {
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

typealias DigitField = Array<IntArray>

fun DigitField.elementAt(row: Int, column: Int): Int =
    this[row][column]

fun DigitField.elementAt(position: Position): Int =
    elementAt(row = position.row, column = position.column)

fun DigitField.isValidPosition(position: Position): Boolean =
    if (position.column < 0 || position.row < 0) {
        false
    } else if (position.column >= this[0].size || position.row >= this.size) {
        false
    } else {
        true
    }


fun inputToDigitField(input: List<String>): DigitField {
    val rows = input.size
    val columns = input.firstOrNull()?.length ?: 0

    val field = Array(rows) { IntArray(columns) }

    input.forEachIndexed { row, line ->
        line.forEachIndexed { column, character ->
            field[row][column] = character.digitToInt()
        }
    }
    return field
}


fun Field.println() {
    println(this.joinToString("\n") { it.joinToString("") })
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

fun <T> testFile(
    testName: String,
    fileName: String,
    execute: (List<String>) -> T,
    expectedValue: T,
    filterBlank: Boolean = true
) {
    val testInput = readInput(fileName)
        .let {
            if (filterBlank) {
                it.filter(String::isNotBlank)
            } else {
                it
            }
        }

    val testOutput = execute(testInput)
    check(testOutput == expectedValue) {
        "$testName: Expected '$expectedValue', got '$testOutput'."
    }
}

fun <T> test(
    testName: String,
    multilineString: String,
    execute: (List<String>) -> T,
    expectedValue: T,
) {
    val testInput = multilineString.lines().filter(String::isNotBlank)
    val testOutput = execute(testInput)
    check(testOutput == expectedValue) {
        "$testName: Expected '$expectedValue', got '$testOutput'."
    }
}

/**
 * 1 2 3    1 4 7
 * 4 5 6 => 2 5 8
 * 7 8 9    3 6 9
 */
fun transpose(xs: Array<CharArray>): Array<CharArray> {
    val cols = xs[0].size
    val rows = xs.size
    return Array(cols) { j ->
        CharArray(rows) { i ->
            xs[i][j]
        }
    }
}

/**
 * 1 2 3    3 6 9
 * 4 5 6 => 2 5 8
 * 7 8 9    1 4 7
 */
fun rotateMinus90(field: Field): Field {
    val ys = field.size
    val xs = field[0].size

    return Array(xs) { x ->
        CharArray(ys) { y ->
            field[y][xs - x - 1]
        }
    }
}

/**
 * 1 2 3    7 4 1
 * 4 5 6 => 8 5 2
 * 7 8 9    9 6 3
 */
fun rotate90(field: Field): Field {
    val ys = field.size
    val xs = field[0].size

    return Array(xs) { x ->
        CharArray(ys) { y ->
            field[ys - y - 1][x]
        }
    }
}
