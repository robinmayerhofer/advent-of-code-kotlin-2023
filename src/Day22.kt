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

        log("Found bricks")
        givenSortedBricks.forEach(::log)

        val droppedBricks = mutableListOf<Brick>()
        val minZ = 0 // ground is 0

        for (brick in givenSortedBricks) {
            val otherZ: Int = droppedBricks.filter { brick.intersectsXY(it) }.maxOfOrNull { it.zRange.last } ?: minZ

            val zDrop = brick.zRange.first - otherZ - 1
            log("zDrop: $zDrop")
            droppedBricks.add(
                element = Brick(
                    start = brick.start.copy(z = brick.start.z - zDrop),
                    end = brick.end.copy(z = brick.end.z - zDrop),
                )
            )
        }
        log("Dropped bricks")
        droppedBricks.forEach(::log)

        // highest droppedBrick is at the end
        val indexToSupportedIndices: Map<Int, List<Int>> = droppedBricks.mapIndexed { index, brick ->
            index to droppedBricks.mapIndexedNotNull { otherIndex, otherBrick ->
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

        log()
        indexToSupportedIndices.forEach {
            log("${it.key} is supporting ${it.value}")
        }

        log()
        indexToSupportingIndices.forEach {
            log("${it.key} is supported by ${it.value}")
        }

        droppedBricks.zip(canRemove).forEach {
            log("Can remove ${it.first}? ${it.second}")
        }

        return canRemove.count { it }
    }

    shouldLog = true

    val a = Brick(start = Position3D(x = 1, y = 1, z = 5), end = Position3D(x = 1, y = 1, z = 6))
    val b = Brick(start = Position3D(x = 0, y = 1, z = 4), end = Position3D(x = 2, y = 1, z = 4))
    check(b.supports(a))
    check((1..3).intersects(2..4))
    check((1..3).intersects(1..1))
    check((1..1).intersects(1..3))
    check((1..1).intersects(0..3))
    check(!(1..1).intersects(0..0))
    check(!(1..1).intersects(2..2))

    testFile(
        "Part 1 Test 1",
        "Day22_test",
        ::part1,
        5,
    )

    shouldLog = false
    val input = readInput("Day22").filter(String::isNotBlank)
    part1(input)
        .also { check(it == 416) }
        .log()


    fun Int.mapToChar() =
        Char(this + 'A'.code)

    check(0.mapToChar() == 'A')

    fun part2(input: List<String>): Int {
        val givenSortedBricks = input
            .map(String::toBrick)
            .sorted()

        log("Found bricks")
        givenSortedBricks.forEach(::log)

        val droppedBricks = mutableListOf<Brick>()
        val minZ = 0 // ground is 0

        for (brick in givenSortedBricks) {
            val otherZ: Int = droppedBricks.filter { brick.intersectsXY(it) }.maxOfOrNull { it.zRange.last } ?: minZ

            val zDrop = brick.zRange.first - otherZ - 1
            log("zDrop: $zDrop")
            droppedBricks.add(
                element = Brick(
                    start = brick.start.copy(z = brick.start.z - zDrop),
                    end = brick.end.copy(z = brick.end.z - zDrop),
                )
            )
        }
        log("Dropped bricks")
        droppedBricks.sort()
        droppedBricks.forEach(::log)

        // highest droppedBrick is at the end
        val indexToSupportedIndices: Map<Int, List<Int>> = droppedBricks.mapIndexed { index, brick ->
            index to droppedBricks.mapIndexedNotNull { otherIndex, otherBrick ->
                if (brick.supports(otherBrick)) {
                    otherIndex
                } else {
                    null
                }
            }
        }.toMap()

        val canRemoveList = MutableList(droppedBricks.size) { true }

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
                canRemoveList[supportingIndices.first()] = false
            }
        }

        log()
        indexToSupportedIndices.forEach {
            log("${it.key} is supporting ${it.value}")
        }

        log()
        indexToSupportingIndices.forEach {
            log("${it.key} is supported by ${it.value}")
        }

        droppedBricks.zip(canRemoveList).forEach {
            log("Can remove ${it.first}? ${it.second}")
        }

        return canRemoveList
            .mapIndexed { index, canRemoveIndexSafely ->
                if (canRemoveIndexSafely) {
                    0
                } else {
                    val affected = mutableSetOf(index)
                    val indicesToCheck = indexToSupportedIndices[index]!!.toMutableSet()
//                    log()
//                    log { "Checked $index. Affected: $affected, left to check $indicesToCheck" }

                    while (indicesToCheck.isNotEmpty()) {
                        val indexToCheck = indicesToCheck.first()

                        if (affected.containsAll(indexToSupportingIndices[indexToCheck]!!)) {
                            affected.add(indexToCheck)
                            indicesToCheck.addAll(indexToSupportedIndices[indexToCheck]!!)
                        }
                        indicesToCheck.remove(indexToCheck)



//                        log { "Checked $indexToCheck. Affected: $affected, left to check $indicesToCheck" }
                    }

                    affected.remove(index) // remove original index again

                    log { "Found ${affected.map { it.mapToChar() }} (${affected.size} bricks) that will drop if we remove brick ${index.mapToChar()}" }
                    affected.size
                }
            }.sum()
    }

//    shouldLog = true
//    testFile(
//        "Part 2 Test 1",
//        "Day22_test",
//        ::part2,
//        7,
//    )
    shouldLog = false
    val input2 = readInput("Day22").filter(String::isNotBlank)
    part2(input2)
        .also { check(it < 89108) { "Too high. $it should be < 89108." } }
        .println()
}
