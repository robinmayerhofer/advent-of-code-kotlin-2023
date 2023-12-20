private typealias GateName = String
private typealias Impulse = Boolean

private const val HIGH = true
private const val LOW = false
private const val BROADCASTER_NAME = "broadcaster"

private interface Gate {
    val name: GateName
    val inputs: Set<GateName>
    val outputs: Set<GateName>

    fun process(fromGate: GateName, impulse: Impulse): Impulse?
}

private data class Broadcaster(
    override val name: GateName,
    override val inputs: Set<GateName>,
    override val outputs: Set<GateName>
) : Gate {
    init {
        check(inputs.isEmpty())
    }

    fun buttonPush(impulse: Impulse) = impulse

    override fun process(fromGate: GateName, impulse: Impulse): Impulse? =
        error("Does not work for broadcaster. It is only the initial entry point")
}

// prefix %
private data class FlipFlop(
    override val name: GateName,
    override val inputs: Set<GateName>,
    override val outputs: Set<GateName>,
    var isOn: Boolean = false,
) : Gate {
    override fun process(fromGate: GateName, impulse: Impulse): Impulse? {
        if (impulse == HIGH) {
            return null
        }
        isOn = !isOn
        return isOn
    }
}

// prefix &
private data class Conjunction(
    override val name: GateName,
    override val inputs: Set<GateName>,
    override val outputs: Set<GateName>,
    val rememberedInputs: MutableMap<GateName, Impulse> = inputs.associateWith { LOW }.toMutableMap(),
) : Gate {
    override fun process(fromGate: GateName, impulse: Impulse): Impulse {
        rememberedInputs[fromGate] = impulse
        val shouldSendLow = rememberedInputs.values.all { it == HIGH }
        return if (shouldSendLow) { LOW }
        else { HIGH }
    }
}

private data class DoNothing(
    override val name: GateName,
    override val inputs: Set<GateName>,
    override val outputs: Set<GateName>,
) : Gate {
    init {
        require(outputs.isEmpty())
    }

    constructor(name: GateName): this(name, emptySet(), emptySet())

    override fun process(fromGate: GateName, impulse: Impulse): Impulse? =
        null
}

private enum class GateType(val regex: Regex) {
    BROADCASTER("($BROADCASTER_NAME)".toRegex()),
    FLIP_FLOP("%([a-z]+)".toRegex()),
    CONJUNCTION("&([a-z]+)".toRegex()),
    DO_NOTHING("([a-z]+)".toRegex()),
    ;
}


fun main() {
    fun parsePart1Input(input: List<String>): Map<GateName, Gate> {
        val rawGates = input.map { line ->
            val (gate, outputs) = line.split("->").map(String::trim)

            val gateType = GateType.entries.first { gateType -> gateType.regex.matches(gate) }
            val gateName = gateType.regex.find(gate)!!.groups[1]!!.value
            val outputGates = outputs.split(",").map(String::trim).toSet()

            Triple(gateName, gateType, outputGates)
        }

        return rawGates.map { (name, type, outputs) ->
            val inputs = rawGates.mapNotNull { (otherName, _, otherOutputs) ->
                if (otherOutputs.contains(name)) {
                    otherName
                } else {
                    null
                }
            }.toSet()

            when (type) {
                GateType.BROADCASTER -> Broadcaster(name, inputs, outputs)
                GateType.CONJUNCTION -> Conjunction(name, inputs, outputs)
                GateType.FLIP_FLOP -> FlipFlop(name, inputs, outputs)
                GateType.DO_NOTHING -> DoNothing(name, inputs, outputs)
            }
        }.associateBy { it.name }
    }

    data class Signal(
        val fromGate: GateName,
        val toGate: GateName,
        val impulse: Impulse,
    )

    fun pushButtonPart1(gateNamesToGates: Map<GateName, Gate>): Pair<Long, Long> {
        log { "Button pushed" }

        val broadcaster = gateNamesToGates[BROADCASTER_NAME] ?: error("Found no '$BROADCASTER_NAME'")

        val initialSignals: List<Signal> = broadcaster.outputs
            .map { output -> Signal(fromGate = BROADCASTER_NAME, toGate = output, impulse = LOW) }

        val deque: ArrayDeque<Signal> = ArrayDeque(initialSignals)

        var lowImpulses = 1L  // button push
        var highImpulses = 0L

        while (deque.isNotEmpty()) {
            val signal = deque.removeFirst()
            log { "Handling $signal" }

            when (signal.impulse) {
                LOW -> lowImpulses += 1
                HIGH -> highImpulses += 1
                else -> error("")
            }

            val gate = gateNamesToGates[signal.toGate] ?: DoNothing(name = signal.toGate)
            val newImpulse = gate.process(signal.fromGate, signal.impulse)
            if (newImpulse != null) {
                deque.addAll(
                    gate.outputs.map { output ->
                        Signal(fromGate = gate.name, toGate = output, impulse = newImpulse)
                    }
                )
            }
        }

        log("Found $lowImpulses low & $highImpulses high impulses")
        log()

        return Pair(lowImpulses, highImpulses)
    }

    fun pushButtonPart2(gateNamesToGates: Map<GateName, Gate>): Boolean {
        log { "Button pushed" }

        val broadcaster = gateNamesToGates[BROADCASTER_NAME] ?: error("Found no '$BROADCASTER_NAME'")

        val initialSignals: List<Signal> = broadcaster.outputs
            .map { output -> Signal(fromGate = BROADCASTER_NAME, toGate = output, impulse = LOW) }

        val deque: ArrayDeque<Signal> = ArrayDeque(initialSignals)


        while (deque.isNotEmpty()) {
            val signal = deque.removeFirst()
            log { "Handling $signal" }

            if (signal.toGate == "rx" && signal.impulse == LOW) {
                return true
            }
//            else if (signal.toGate == "rx") {
//                println("Found rx but with high pulse")
//            }

            val gate = gateNamesToGates[signal.toGate] ?: DoNothing(name = signal.toGate)
            val newImpulse = gate.process(signal.fromGate, signal.impulse)
            if (newImpulse != null) {
                deque.addAll(
                    gate.outputs.map { output ->
                        Signal(fromGate = gate.name, toGate = output, impulse = newImpulse)
                    }
                )
            }
        }

        return false
    }

    fun part1(input: List<String>): Long {
        val gateNamesToGates = parsePart1Input(input)
        log { gateNamesToGates }

        val result = (1..1000)
            .map { pushButtonPart1(gateNamesToGates) }
            .fold(Pair(0L, 0L)) { acc, element ->
                (acc.first + element.first) to (acc.second + element.second)
            }

        return result.first * result.second
    }

    fun part2(input: List<String>): Long {
        val gateNamesToGates = parsePart1Input(input)
        log { gateNamesToGates }

        var buttonPushes = 0
        while (true) {
            if (buttonPushes % 1_000_000 == 0) {
                println("$buttonPushes button pushes")
            }
            buttonPushes += 1
            val reachedEnd = pushButtonPart2(gateNamesToGates)
            if (reachedEnd) {
                return buttonPushes.toLong()
            }
        }
    }

    val inverter = Conjunction(
        name = "inv",
        inputs = setOf("a"),
        outputs = setOf("b"),
    )
    check(inverter.process(fromGate = "a", impulse = LOW) == HIGH)
    check(inverter.process(fromGate = "a", impulse = LOW) == HIGH)
    check(inverter.process(fromGate = "a", impulse = HIGH) == LOW)
    check(inverter.process(fromGate = "a", impulse = HIGH) == LOW)
    check(inverter.process(fromGate = "a", impulse = LOW) == HIGH)

    testFile(
        "Part 1 Test 1",
        "Day20_test",
        ::part1,
        32000000L,
        filterBlank = true,
    )

    val input = readInput("Day20").filter(String::isNotBlank)
    part1(input).println()

//    testFile(
//        "Part 2 Test 1",
//        "Day20_test",
//        ::part2,
//        1,
//        filterBlank = true,
//    )
    shouldLog = false
    val input2 = readInput("Day20").filter(String::isNotBlank)
    part2(input2).println()
}
