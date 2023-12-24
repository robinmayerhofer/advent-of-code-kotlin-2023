fun main() {
    shouldLog = true

    data class Line3D(
        val pX: Long,
        val pY: Long,
        val pZ: Long,
        val dX: Long,
        val dY: Long,
        val dZ: Long,
    )

    class MyLine2D(
        val px: Long,
        val py: Long,
        val dx: Long,
        val dy: Long,
    ) {
        // Line = k*x + d
        val k: Double
        val d: Double

        init {
            // k * px1 + d = py1 => d = py1 - k * px1
            // k * px2 + d = py2
            // --
            // k * px2 + py1 - k * px1 = py2
            // k = (py2 - py1) / (px2 - px1)
            // d = py1 - k * px1
            val px1 = px
            val py1 = py
            val px2 = px + dx
            val py2 = py + dy

            k = if ((px2 - px1) == 0L) {
                0.toDouble()
            } else {
                (py2 - py1).toDouble() / (px2 - px1).toDouble()
            }
            d = py1 - k * px1
        }

        fun inTheFuture(ix: Double, iy: Double): Boolean =
            // x =>
            if (ix.toLong() == px) {
                true
            } else if (px > ix) {
                dx < 0
            } else if (px < ix) {
                dx > 0
            } else {
                error("Invalid")
            }

        fun intersects(other: MyLine2D): Pair<Double, Double>? {
            // k1*x + d1 = y
            // k2*x + k2 = y
            // k1*x + d1 = k2*x + d2
            // x*(k1-k2) + (d1-d2) = 0
            // x = (d2-d1) / (k1-k2)
            // y = k1*x + d

            return if (k == other.k) {
                null
            } else {
                val k1 = k
                val d1 = d
                val k2 = other.k
                val d2 = other.d

                val x = (d2 - d1) / (k1 - k2)
                val y = k1 * x + d
                Pair(x, y)
            }
        }

        override fun toString(): String {
            return "MyLine2D(px=$px, py=$py, dx=$dx, dy=$dy, k=$k, d=$d)"
        }


    }

    fun validIntersection(
        index1: Int, line1: MyLine2D,
        index2: Int, line2: MyLine2D,
        testMinX: Long, testMaxX: Long,
        testMinY: Long, testMaxY: Long,
    ): Boolean {
        val intersectionPoint = line1.intersects(line2)
        if (intersectionPoint == null) {
            return false
        }
        val ix = intersectionPoint.first
        val iy = intersectionPoint.second
        if (ix < testMinX || ix > testMaxX || iy < testMinY || iy > testMaxY) {
            log { "Found OUTSIDE intersection between Line $index1 $line1 and Line $index2 $line2 @ ($ix, $iy)" }
            return false
        }

        if (!line1.inTheFuture(ix, iy) ||
            !line2.inTheFuture(ix, iy)
        ) {
            log { "Found PAST INSIDE intersection between Line $index1 $line1 and Line $index2 $line2 @ ($ix, $iy)" }
            return false
        }
        log { "Found INSIDE intersection between Line $index1 $line1 and Line $index2 $line2 @ ($ix, $iy)" }

        return true
    }


    fun part1(
        input: List<String>,
        testMinX: Long, testMaxX: Long,
        testMinY: Long, testMaxY: Long,
    ): Int {
        val lines = input.map {
            val numbers = it.findAllNumbersLong()
            MyLine2D(px = numbers[0], py = numbers[1], dx = numbers[3], dy = numbers[4])
        }

        var relevantIntersections = 0
        for ((index1, line1) in lines.withIndex()) {
            for ((index2, line2) in lines.withIndex()) {
                if (index2 <= index1) {
                    continue
                }

                if (
                    validIntersection(
                        index1, line1,
                        index2, line2,
                        testMinX, testMaxX,
                        testMinY, testMaxY
                    )
                ) {
                    relevantIntersections += 1
                }
            }
        }
        return relevantIntersections
    }

    fun part2(input: List<String>): Int =
        input.sumOf {
            it.length
        }

    //Hailstones' paths will cross inside the test area (at x=14.333, y=15.333).
    //
    //Hailstone A: 19, 13, 30 @ -2, 1, -2
    //Hailstone B: 20, 25, 34 @ -2, -2, -4
    //Hailstones' paths will cross inside the test area (at x=11.667, y=16.667).
    var hailA = MyLine2D(19, 13, -2, 1)
    var hailB = MyLine2D(18, 19, -1, -1)

    println()
    hailA.intersects(hailB).println()
    validIntersection(
        0, hailA,
        1, hailB,
        7, 27,
        7, 27
    )

    println()
    //Hailstone A: 19, 13, 30 @ -2, 1, -2
    //Hailstone B: 12, 31, 28 @ -1, -2, -1
    //Hailstones' paths will cross outside the test area (at x=6.2, y=19.4).
    //
    //Hailstone A: 19, 13, 30 @ -2, 1, -2
    //Hailstone B: 20, 19, 15 @ 1, -5, -3
    //Hailstones' paths crossed in the past for hailstone A.
    //
    //Hailstone A: 18, 19, 22 @ -1, -1, -2
    //Hailstone B: 20, 25, 34 @ -2, -2, -4
    //Hailstones' paths are parallel; they never intersect.
    //
    //Hailstone A: 18, 19, 22 @ -1, -1, -2
    //Hailstone B: 12, 31, 28 @ -1, -2, -1
    //Hailstones' paths will cross outside the test area (at x=-6, y=-5).
    //
    //Hailstone A: 18, 19, 22 @ -1, -1, -2
    //Hailstone B: 20, 19, 15 @ 1, -5, -3
    //Hailstones' paths crossed in the past for both hailstones.
    //
    //Hailstone A: 20, 25, 34 @ -2, -2, -4
    //Hailstone B: 12, 31, 28 @ -1, -2, -1
    //Hailstones' paths will cross outside the test area (at x=-2, y=3).
    //
    //Hailstone A: 20, 25, 34 @ -2, -2, -4
    //Hailstone B: 20, 19, 15 @ 1, -5, -3
    //Hailstones' paths crossed in the past for hailstone B.
    //
    //Hailstone A: 12, 31, 28 @ -1, -2, -1
    //Hailstone B: 20, 19, 15 @ 1, -5, -3
    //Hailstones' paths crossed in the past for both hailstones.

    testFile(
        "Part 1 Test 1",
        "Day24_test",
        {
            part1(
                it,
                testMinX = 7, testMaxX = 27,
                testMinY = 7, testMaxY = 27,
            )
        },
        2,
        filterBlank = false,
    )

    val input = readInput("Day24").filter(String::isNotBlank)
    part1(
        input,
        testMinX = 200000000000000, testMaxX = 400000000000000,
        testMinY = 200000000000000, testMaxY = 400000000000000,
    )
        .also { check(it != 4842) { "$it should not be 4842." } }
        .println()

//    testFile(
//        "Part 2 Test 1",
//        "Day24_test",
//        ::part2,
//        1,
//        filterBlank = false,
//    )
//    val input2 = readInput("Day24").filter(String::isNotBlank)
//    part2(input2).println()
}