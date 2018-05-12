package idea.forest.ui

object ForestLogPrinter {
    val log = StringBuilder()

    fun getUpdates(): String = log.toString().also { log.setLength(0) }
    fun append(line: String) = print(line).also { log.append(line) }
    fun appendln(line: String = "") = println(line).also { log.appendln(line) }
}
