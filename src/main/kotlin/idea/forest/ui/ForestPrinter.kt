package idea.forest.ui

import idea.forest.LogPrinter

class AppendingLogPrinter: LogPrinter {
    override fun println(str: Any?) {
        kotlin.io.println(str)
        log.appendln(str)
    }
    
    override fun print(str: Any?) {
        kotlin.io.print(str)
        log.append(str)
    }
    
    val log = StringBuilder()

    fun getUpdates(): String = log.toString().also { log.setLength(0) }
}


val appendingLogPrinter: AppendingLogPrinter
    get() = idea.forest.log as AppendingLogPrinter




