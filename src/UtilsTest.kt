fun <T> testFile(
    testName: String,
    fileName: String,
    execute: (List<String>) -> T,
    expectedValue: T,
    filterBlank: Boolean = true
) = measure {
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
) = measure {
    val testInput = multilineString.lines().filter(String::isNotBlank)
    val testOutput = execute(testInput)
    check(testOutput == expectedValue) {
        "$testName: Expected '$expectedValue', got '$testOutput'."
    }
}
