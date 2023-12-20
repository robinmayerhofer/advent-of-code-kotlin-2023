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

    fun pushButtonPart2(gateNamesToGates: Map<GateName, Gate>, buttonPush: Int): Boolean {
        log { "Button pushed" }

        val broadcaster = gateNamesToGates[BROADCASTER_NAME] ?: error("Found no '$BROADCASTER_NAME'")

        val initialSignals: List<Signal> = broadcaster.outputs
            .map { output -> Signal(fromGate = BROADCASTER_NAME, toGate = output, impulse = LOW) }

        val deque: ArrayDeque<Signal> = ArrayDeque(initialSignals)

        while (deque.isNotEmpty()) {
            val signal = deque.removeFirst()
            log { "Handling $signal" }

            if (signal.fromGate =="tg" && signal.toGate == "rx" && signal.impulse == LOW) {
                return true
            }

            // "db", "ln", "vq", "tf"
            if (signal.fromGate == "tf" && signal.toGate == "tg" && signal.impulse == HIGH) {
                println("HIGH to ln in button push #$buttonPush: $signal")
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
        repeat(20_000) { step ->
            if (buttonPushes % 1_000_000 == 0) {
                println("$buttonPushes button pushes")
            }
            buttonPushes += 1
            val reachedEnd = pushButtonPart2(gateNamesToGates, buttonPushes)
            if (reachedEnd) {
                return buttonPushes.toLong()
            }
        }

        return buttonPushes.toLong()
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

    shouldLog = false
    val input2 = readInput("Day20").filter(String::isNotBlank)
    // Input has end at rx, which has the "&tg" before it with 4 inputs
    // tg needs to get 4 high inputs so that it sends out a LOW signal to rx
    // each of the 4 inputs gets high periodically (run 20-100k steps and observe when a HIGH is sent from each of the 4 previous steps to tg)
    // all the cycle lengths are primes, multiply them and get the answer

    // the cycle length can also be visually observed because the structures are counters with a reset
    // when all inputs for the "NAND" (called "conjunction" in the puzzle are true we send "low"
    // then we have an inverter for each reset NAND that hits a NAND "tg"
    // then "tg" hits "rx" => when the counters are reset at the same time we get a low impulse to "rx"

    // for example, one circle starts at "km" directly after the broadcaster
    // it goes "km -> dr -> kg -> lv -> jc -> qr -> dk -> vj -> ps -> xf -> bd -> gg"
    //          0     1     2     3     4     5     6     7     8     9     10    11
    // km hits tp => 2^0
    // dr does not => 0
    // kg does not => 0
    // lv does => 2^3
    // jc does => 2^4
    // qr does not => 0
    // dk does => 2^6
    // vj does not => 0
    // ps does => 2^8
    // xf does not => 2^9
    // bd does not => 2^10
    // gg does not => 2^11
    // in total: 11101011001 => 3929. Cycle length 1
    // the cycle lengths are: 3929, 4091, 4007, 3923 => 252.667.369.442.479

    part2(input2).println()
}
