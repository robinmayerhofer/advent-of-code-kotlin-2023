import kotlin.math.max
import kotlin.math.min

fun main() {
    data class Position3D(
        val x: Int,
        val y: Int,
        val z: Int,
    ) : Comparable<Position3D> {

        override fun compareTo(other: Position3D): Int {
            if (z != other.z) {
                return z - other.z
            }
            if (y != other.y) {
                return y - other.y
            }
            return x - other.x
        }
    }

    fun minPosition(a: Position3D, b: Position3D): Position3D =
        if (a < b) {
            a
        } else {
            b
        }

    fun IntRange.intersects(other: IntRange): Boolean =
        if (this.first >= other.first && this.first <= other.last) {
            true
        } else if (other.first >= this.first && other.first <= this.last) {
            true
        } else {
            false
        }

    data class Brick(
        val start: Position3D,
        val end: Position3D,
    ) : Comparable<Brick> {

        val xRange: IntRange = min(start.x, end.x)..max(start.x, end.x)
        val yRange: IntRange = min(start.y, end.y)..max(start.y, end.y)
        val zRange: IntRange = min(start.z, end.z)..max(start.z, end.z)

        fun intersectsXY(other: Brick): Boolean =
            xRange.intersects(other.xRange) && yRange.intersects(other.yRange)


        fun intersects(other: Brick): Boolean =
            xRange.intersects(other.xRange) && yRange.intersects(other.yRange) && zRange.intersects(other.zRange)

        fun supports(other: Brick): Boolean =
            if (zRange.last == other.zRange.first - 1) {
                intersectsXY(other)
            } else {
                false
            }

        override fun compareTo(other: Brick): Int {
            val min = minPosition(start, end)
            val otherMin = minPosition(other.start, other.end)

            return min.compareTo(otherMin)
        }
    }

    fun String.toBrick(): Brick {
        val (start, end) = this.split("~")
            .map { it.findAllNumbers() }

        return Brick(
            start = Position3D(
                x = start[0],
                y = start[1],
                z = start[2],
            ),
            end = Position3D(
                x = end[0],
                y = end[1],
                z = end[2],
            ),
        )
    }

    fun part1(input: List<String>): Int {
        val givenSortedBricks = input
            .map(String::toBrick)
            .sorted()

        println("Found bricks")
        givenSortedBricks.asReversed().forEach(::println)

        val droppedBricks = mutableListOf<Brick>()
        val minZ = 1

        for (brick in givenSortedBricks) {
            val blockingBrick = droppedBricks.firstOrNull { it.intersectsXY(brick) }

            val zDrop = if (blockingBrick == null) {
                brick.zRange.first - minZ
            } else {
                brick.zRange.first - blockingBrick.zRange.last - 1
            }
            println("zDrop: $zDrop")
            droppedBricks.add(
                index = 0,
                element = Brick(
                    start = brick.start.copy(z = brick.start.z - zDrop),
                    end = brick.end.copy(z = brick.end.z - zDrop),
                )
            )
        }
        println("Dropped bricks")
        droppedBricks.sortDescending()
        droppedBricks.forEach(::println)

        // highest droppedBrick is at the start
        val indexToSupportedIndices: Map<Int, List<Int>> = droppedBricks.mapIndexed { index, brick ->
            index to droppedBricks.take(index).mapIndexedNotNull { otherIndex, otherBrick ->
                if (brick.supports(otherBrick)) {
                    otherIndex
                } else {
                    null
                }
            }
        }.toMap()

        val canRemove = MutableList(droppedBricks.size) { true }

        val indexToSupportingIndices =
            indexToSupportedIndices.entries.fold(mutableMapOf()) { acc: MutableMap<Int, MutableSet<Int>>, (supportingIndex: Int, supportedIndices: List<Int>) ->
                supportedIndices.forEach { supportedIndex ->
                    val supportedBySet = acc.getOrDefault(supportedIndex, mutableSetOf())
                    supportedBySet.add(supportingIndex)
                    acc[supportedIndex] = supportedBySet
                }
                acc
            }

        indexToSupportingIndices.values.forEach { supportingIndices ->
            if (supportingIndices.size == 1) {
                canRemove[supportingIndices.first()] = false
            }
        }

        println()
        indexToSupportedIndices.forEach {
            println("${it.key} is supporting ${it.value}")
        }

        println()
        indexToSupportingIndices.forEach {
            println("${it.key} is supported by ${it.value}")
        }

        droppedBricks.zip(canRemove).forEach {
            println("Can remove ${it.first}? ${it.second}")
        }

        return canRemove.count { it }
    }

    val a = Brick(start = Position3D(x = 1, y = 1, z = 5), end = Position3D(x = 1, y = 1, z = 6))
    val b = Brick(start = Position3D(x = 0, y = 1, z = 4), end = Position3D(x = 2, y = 1, z = 4))
    check(b.supports(a))

    shouldLog = true
    testFile(
        "Part 1 Test 1",
        "Day22_test",
        ::part1,
        5,
    )

//    val input = readInput("Day22").filter(String::isNotBlank)
//    part1(input)
//        .also { check(it < 437) { "Too high. Expected $it < 437." } }
//        .println()

//    fun part2(input: List<String>): Int =
//        input.sumOf {
//            it.length
//        }
//
//    testFile(
//        "Part 2 Test 1",
//        "Day22_test",
//        ::part2,
//        1,
//    )
//    val input2 = readInput("Day22").filter(String::isNotBlank)
//    part2(input2).println()
}
