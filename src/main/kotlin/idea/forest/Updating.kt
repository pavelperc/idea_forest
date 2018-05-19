package idea.forest


/**
 * Created by pavel on 22.04.2018.
 */

/**
 * Any Animal or tree, or logWriter should implement this interface
 * and add itself to [Updater]
 */
interface Updatable {
    
    val updateSpeed: UpdateSpeed
    
    fun update()
    
    /** If this fun returns false - updater removes this updatable and doesn't update it anymore
     * This fun is being checked before update invocation.*/
    fun shouldUpdate() = true
    
    companion object {
        fun createFastUpdatable(updateFun: () -> Unit) =
            object : Updatable {
                override val updateSpeed = UpdateSpeed.FAST
                override fun update() = updateFun()
            }
    }
}

enum class UpdateSpeed {
    FAST, MEDIUM, LONG
}


object Updater {
    
    private val tasks = mutableListOf<Updatable>()
    
    
    fun addUpdatable(updatable: Updatable) {
        tasks.add(updatable)
    }
    
    fun addUpdatableToBeginning(updatable: Updatable) {
        tasks.add(0, updatable)
    }
    
    /** clears all updatable [tasks] and resets [period]*/
    fun resetAll() {
        tasks.clear()
        period = 0
    }
    
    var period = 0
        private set
    
    /** One tick of life*/
    fun tick() {
        
        
        invokeAll(UpdateSpeed.FAST)
        if (period % 2 == 0) {
            invokeAll(UpdateSpeed.MEDIUM)
        }
        if (period % 4 == 0) {
            invokeAll(UpdateSpeed.LONG)
        }
        
        log.println("---Ended period ${period}---\n\n")
        period++
    }
    
    /** Starts updater: invokes [tick] [repeatCount] times with [delay].
     * ATTENTION: it blocks the thread it was invoked from!!
     * For updating without blocking you can call just [tick]*/
    fun start(repeatCount: Int = 100, delay: Long = 10) {
        period = 0
        while (period < repeatCount) {
            Thread.sleep(delay)
            tick()
        }
    }
    
    private fun invokeAll(updateSpeed: UpdateSpeed) {
        val forRemoval = mutableListOf<Updatable>()
        tasks
            .filter { it.updateSpeed == updateSpeed }
            .forEach {
                if (it.shouldUpdate())
                    it.update()
                else
                    forRemoval.add(it)
            }
        
        tasks.removeAll(forRemoval)
    }
}