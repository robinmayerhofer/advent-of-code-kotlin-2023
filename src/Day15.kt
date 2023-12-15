fun main() {

    fun String.hash(): Int =
        fold(initial = 0) { acc: Int, c: Char ->
            ((acc + c.code) * 17) % 256
        }

    fun part1(input: String): Int =
        input
            .split(",")
            .sumOf(String::hash)

    fun printBoxes(boxes: Array<MutableList<String>>) {
        boxes.mapIndexed { boxNumber, slots ->
            val str = slots.joinToString(" ") { "[${it.replace("=", " ")}]" }
            if (str.isNotEmpty()) {
                println("Box ${boxNumber}: $str")
            }
        }
    }

    fun part2(input: String): Long {
        val boxes = Array(256) {
            mutableListOf<String>()
        }
        input
            .split(",")
            .forEach { instruction ->
                if ("=" in instruction) {
                    val id = instruction.split("=")[0]
                    val idHash = id.hash()
                    val index = boxes[idHash].indexOfFirst { it.startsWith("$id=") }
                    if (index >= 0) {
                        boxes[idHash][index] = instruction
                    } else {
                        boxes[idHash].add(instruction)
                    }
                } else if ("-" in instruction) {
                    val id = instruction.split("-")[0]
                    val idHash = id.hash()
                    boxes[idHash].removeAll { it.startsWith("$id=") }
                } else {
                    error("?")
                }

                println("After \"$instruction\":")
                printBoxes(boxes)
                println()
            }

        return boxes
            .flatMapIndexed { box, slots ->
                slots.mapIndexed { slot, instruction ->
                    val value = (box + 1) * (slot + 1) * (instruction.split("=")[1].toLong())
                    println("${instruction}: $value")
                    value
                }
            }.sum()
    }

    part1("HASH").also { check(it == 52) { "Result was not 52 but $it" } }

    val input = readInput("Day15").filter(String::isNotBlank)
    part1(input[0]).println()

    part2("rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7")
        .also { check(it == 145L) }
        .println()
    part2(input[0])
        .also { check(it == 296921L) { "Wrong result $it, should be 296921" } }
        .println()
}
