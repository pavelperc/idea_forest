package idea.forest

/**
 * Created by pavel on 13.05.2018.
 */
interface LogPrinter {
    
    // TODO Can be expanded with different types of logs.
    fun println(str: Any? = "")
    fun print(str: Any? = "")
}

/** Every Animal or tree. calls log.println() for non debug output.
 * For UI this log should be reassigned with another implementation of [LogPrinter]*/
var log: LogPrinter = object : LogPrinter {
    override fun println(str: Any?) {
        kotlin.io.println(str)
    }
    
    override fun print(str: Any?) {
        kotlin.io.print(str)
    }
}