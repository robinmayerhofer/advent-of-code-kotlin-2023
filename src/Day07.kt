fun main() {

    data class Card(
        // A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
        val symbol: Char,
        val realSymbol: Char = symbol,
        val jValue: Int = 11,
    ) {
        val strength: Int by lazy {
            when (realSymbol) {
                'A' -> 14
                'K' -> 13
                'Q' -> 12
                'J' -> jValue
                'T' -> 10
                else -> realSymbol.digitToInt()
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Card) return false

            if (symbol != other.symbol) return false

            return true
        }

        override fun hashCode(): Int {
            return symbol.hashCode()
        }
    }

    abstract class Hand(open val cards: List<Card>, val strength: Int) : Comparable<Hand> {

        override fun toString(): String {
            val className = javaClass.name.split("$").last()
            val cardString = cards.map { it.realSymbol }.joinToString("")
            return "$className $cardString"
        }

        override fun compareTo(other: Hand): Int {
            val strengthDiff = strength - other.strength
            if (strengthDiff != 0) {
                return strengthDiff
            }

            return cards.zip(other.cards)
                .first { (c1, c2) -> c1.realSymbol != c2.realSymbol }
                .let { (c1, c2) -> c1.strength - c2.strength }
        }
    }

    class FiveOfAKind(override val cards: List<Card>) : Hand(cards, strength = 7)

    class FourOfAKind(override val cards: List<Card>) : Hand(cards, strength = 6)

    class FullHouse(override val cards: List<Card>) : Hand(cards, strength = 5)

    class ThreeOfAKind(override val cards: List<Card>) : Hand(cards, strength = 4)

    class TwoPairs(override val cards: List<Card>) : Hand(cards, strength = 3)

    class OnePair(override val cards: List<Card>) : Hand(cards, strength = 2)

    class HighCard(override val cards: List<Card>) : Hand(cards, strength = 1)

    fun cardsToHandP1(cards: List<Card>): Hand {
        val setOfCards = cards.toSet()

        val max = setOfCards.maxOf { card: Card ->
            cards.count { it == card }
        }

        return when (max) {
            5 -> FiveOfAKind(cards)
            4 -> FourOfAKind(cards)
            3 -> if (setOfCards.size == 2) {
                FullHouse(cards)
            } else {
                ThreeOfAKind(cards)
            }

            2 -> if (setOfCards.size == 3) {
                TwoPairs(cards)
            } else {
                OnePair(cards)
            }

            else -> HighCard(cards)

        }
    }

    fun cardsToHandP2(cards: List<Card>): Hand {
        val setOfCards = cards.filter { it.symbol != 'J' }.toSet()

        val maxOccuringCard = setOfCards.maxByOrNull { card: Card ->
            cards.count { it == card }
        } ?: return FiveOfAKind(cards)

        val mappedCards = cards.map {
            if (it.symbol != 'J') {
                it
            } else {
                it.copy(symbol = maxOccuringCard.symbol)
            }
        }

        return cardsToHandP1(mappedCards)
    }

    fun part1(input: List<String>): Long =
        input
            .filter(String::isNotBlank)
            .map { line ->
                val (handString, bidString) = line.split(" ")
                val cards = handString
                    .trim()
                    .map(::Card)

                val hand = cardsToHandP1(cards)
                val bid = bidString.toLong()

                hand to bid
            }
            .sortedBy { (hand, _) -> hand }
            .mapIndexed { index, (_, bid) ->
                (index + 1L) * bid
            }
            .sum()


    fun part2(input: List<String>): Long =
        input
            .asSequence()
            .filter(String::isNotBlank)
            .map { line ->
                val (handString, bidString) = line.split(" ")
                val cards = handString
                    .trim()
                    .map { Card(symbol = it, jValue = 0) }

                val hand = cardsToHandP2(cards)
                val bid = bidString.toLong()

                assert(hand.cards.all { it.jValue == 0 })

                hand to bid
            }
            .sortedBy { (hand, _) -> hand }
            .mapIndexed { index, (hand, bid) ->
                println("${index + 1}: $hand (bid $bid)")
                (index.toLong() + 1L) * bid
            }
            .sum()


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    val testOutput = part1(testInput)
    val expectedTestOutput = 6440L
    check(testOutput == expectedTestOutput) {
        "Part 1 Tests: Expected $expectedTestOutput, got $testOutput"
    }

    val input = readInput("Day07")
    part1(input).println()

    val testOutput2 = part2(testInput)
    val expectedTestOutput2 = 5905L
    check(testOutput2 == expectedTestOutput2) {
        "Part 2 Tests: Expected $expectedTestOutput2, got $testOutput2"
    }

    val resultPart2 = part2(input)

    resultPart2.println()
}
