var shouldLog = false

fun Any?.log() = log(this)

@JvmName("logAny")
fun log(message: Any? = null) {
    if (shouldLog) {
        println(message)
    }
}

@JvmName("logAnyLazy")
fun log(message: (() -> Any?)) {
    if (shouldLog) {
        println(message())
    }
}
