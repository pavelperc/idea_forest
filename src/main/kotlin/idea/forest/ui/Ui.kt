package idea.forest.ui

import idea.forest.*
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*

class ForestView : View("Idea Forest") {
    private val TextField.i get() = text.trim().toInt()
    private val TextField.d get() = text.trim().toDouble()
    
    //<editor-fold defaultstate="collapsed" desc="UI objects">
    override val root: BorderPane by fxml()
    
    private val firStartAmount: TextField by fxid()
    //    private val firLuckRate: TextField by fxid()
//    private val firFrom: TextField by fxid()
//    private val firTo: TextField by fxid()
    private val pineStartAmount: TextField by fxid()
    //    private val pineLuckRate: TextField by fxid()
//    private val pineFrom: TextField by fxid()
//    private val pineTo: TextField by fxid()
    private val oakStartAmount: TextField by fxid()
    //    private val oakLuckRate: TextField by fxid()
//    private val oakFrom: TextField by fxid()
//    private val oakTo: TextField by fxid()
    private val birchStartAmount: TextField by fxid()
    //    private val birchLuckRate: TextField by fxid()
//    private val birchFrom: TextField by fxid()
//    private val birchTo: TextField by fxid()
    private val mapleStartAmount: TextField by fxid()
    //    private val mapleLuckRate: TextField by fxid()
//    private val mapleFrom: TextField by fxid()
//    private val mapleTo: TextField by fxid()
    private val walnutStartAmount: TextField by fxid()
//    private val walnutLuckRate: TextField by fxid()
//    private val walnutFrom: TextField by fxid()
//    private val walnutTo: TextField by fxid()
    
    private val squirrelStartAmount: TextField by fxid()
    //    private val squirrelLuckRate: TextField by fxid()
    private val squirrelFrom: TextField by fxid()
    private val squirrelTo: TextField by fxid()
    private val squirrelYearsFrom: TextField by fxid()
    private val squirrelYearsTo: TextField by fxid()
    private val chipmunkStartAmount: TextField by fxid()
    //    private val chipmunkLuckRate: TextField by fxid()
    private val chipmunkFrom: TextField by fxid()
    private val chipmunkTo: TextField by fxid()
    private val chipmunkYearsFrom: TextField by fxid()
    private val chipmunkYearsTo: TextField by fxid()
    private val badgerStartAmount: TextField by fxid()
    //    private val badgerLuckRate: TextField by fxid()
    private val badgerFrom: TextField by fxid()
    private val badgerTo: TextField by fxid()
    private val badgerYearsFrom: TextField by fxid()
    private val badgerYearsTo: TextField by fxid()
    private val flyingStartAmount: TextField by fxid()
    //    private val flyingLuckRate: TextField by fxid()
    private val flyingFrom: TextField by fxid()
    private val flyingTo: TextField by fxid()
    private val flyingYearsFrom: TextField by fxid()
    private val flyingYearsTo: TextField by fxid()
    private val woodpeckerStartAmount: TextField by fxid()
    //    private val woodpeckerLuckRate: TextField by fxid()
    private val woodpeckerFrom: TextField by fxid()
    private val woodpeckerTo: TextField by fxid()
    private val woodpeckerYearsFrom: TextField by fxid()
    private val woodpeckerYearsTo: TextField by fxid()
    
    private val reset: Button by fxid()
    private val stepDelay: TextField by fxid()
    private val stepCount: TextField by fxid()
    private val tick: Button by fxid()
    
    val logArea: TextArea by fxid()
    //</editor-fold>
    
    private val randoms = ForestRandoms()
    lateinit var forest: Forest
    
    init {
        reset()
        addListeners()
        updateSettings()
    }
    
    //<editor-fold defaultstate="collapsed" desc="private fun addListeners() {...}">
    private fun addListeners() {
        reset.onAction = EventHandler { reset() }
        tick.onAction = EventHandler { tick() }
        
        firStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        firLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        firFrom.onKeyPressed = EventHandler { updateSettings() }
//        firTo.onKeyPressed = EventHandler { updateSettings() }
        pineStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        pineLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        pineFrom.onKeyPressed = EventHandler { updateSettings() }
//        pineTo.onKeyPressed = EventHandler { updateSettings() }
        oakStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        oakLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        oakFrom.onKeyPressed = EventHandler { updateSettings() }
//        oakTo.onKeyPressed = EventHandler { updateSettings() }
        birchStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        birchLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        birchFrom.onKeyPressed = EventHandler { updateSettings() }
//        birchTo.onKeyPressed = EventHandler { updateSettings() }
        mapleStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        mapleLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        mapleFrom.onKeyPressed = EventHandler { updateSettings() }
//        mapleTo.onKeyPressed = EventHandler { updateSettings() }
        walnutStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        walnutLuckRate.onKeyPressed = EventHandler { updateSettings() }
//        walnutFrom.onKeyPressed = EventHandler { updateSettings() }
//        walnutTo.onKeyPressed = EventHandler { updateSettings() }
        
        squirrelStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        squirrelLuckRate.onKeyPressed = EventHandler { updateSettings() }
        squirrelFrom.onKeyPressed = EventHandler { updateSettings() }
        squirrelTo.onKeyPressed = EventHandler { updateSettings() }
        squirrelYearsFrom.onKeyPressed = EventHandler { updateSettings() }
        squirrelYearsTo.onKeyPressed = EventHandler { updateSettings() }
        chipmunkStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        chipmunkLuckRate.onKeyPressed = EventHandler { updateSettings() }
        chipmunkFrom.onKeyPressed = EventHandler { updateSettings() }
        chipmunkTo.onKeyPressed = EventHandler { updateSettings() }
        chipmunkYearsFrom.onKeyPressed = EventHandler { updateSettings() }
        chipmunkYearsTo.onKeyPressed = EventHandler { updateSettings() }
        badgerStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        badgerLuckRate.onKeyPressed = EventHandler { updateSettings() }
        badgerFrom.onKeyPressed = EventHandler { updateSettings() }
        badgerTo.onKeyPressed = EventHandler { updateSettings() }
        badgerYearsFrom.onKeyPressed = EventHandler { updateSettings() }
        badgerYearsTo.onKeyPressed = EventHandler { updateSettings() }
        flyingStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        flyingLuckRate.onKeyPressed = EventHandler { updateSettings() }
        flyingFrom.onKeyPressed = EventHandler { updateSettings() }
        flyingTo.onKeyPressed = EventHandler { updateSettings() }
        flyingYearsFrom.onKeyPressed = EventHandler { updateSettings() }
        flyingYearsTo.onKeyPressed = EventHandler { updateSettings() }
        woodpeckerStartAmount.onKeyPressed = EventHandler { updateSettings() }
//        woodpeckerLuckRate.onKeyPressed = EventHandler { updateSettings() }
        woodpeckerFrom.onKeyPressed = EventHandler { updateSettings() }
        woodpeckerTo.onKeyPressed = EventHandler { updateSettings() }
        woodpeckerYearsFrom.onKeyPressed = EventHandler { updateSettings() }
        woodpeckerYearsTo.onKeyPressed = EventHandler { updateSettings() }
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="private fun updateSettings() {...}">
    private fun updateSettings() {
        try {  // TODO: Do something w/ inputs
            
            /*firLuckRate.d
            firFrom.i
            firTo.i

            pineLuckRate.d
            pineFrom.i
            pineTo.i

            oakLuckRate.d
            oakFrom.i
            oakTo.i

            birchLuckRate.d
            birchFrom.i
            birchTo.i

            mapleLuckRate.d
            mapleFrom.i
            mapleTo.i

            walnutLuckRate.d
            walnutFrom.i
            walnutTo.i*/
            
            /** setup frequencies for FIR, PINE, OAK, BIRCH, MAPLE, WALNUT */
            randoms.randomTreeType = RandomEnum(
                intArrayOf(
                    firStartAmount.i,
                    pineStartAmount.i,
                    oakStartAmount.i,
                    birchStartAmount.i,
                    mapleStartAmount.i,
                    walnutStartAmount.i
                )
            )
            
            randoms.treeRandoms.animalRandoms.birthCount = {
                when (it) {
                    AnimalType.Squirrel -> RandomInt(squirrelFrom.i..squirrelTo.i)
                    AnimalType.Chipmunk -> RandomInt(chipmunkFrom.i..chipmunkTo.i)
                    AnimalType.Badger -> RandomInt(badgerFrom.i..badgerTo.i)
                    AnimalType.FlyingSquirrel -> RandomInt(flyingFrom.i..flyingTo.i)
                    AnimalType.Woodpecker -> RandomInt(woodpeckerFrom.i..woodpeckerTo.i)
                    
                    AnimalType.Kite -> RandomInt(0..2)
                    AnimalType.Wolf -> RandomInt(0..2)
                }
            }
            randoms.treeRandoms.animalRandoms.maxAge = {
                when (it) {
                    AnimalType.Squirrel -> RandomInt(squirrelYearsFrom.i..squirrelYearsTo.i)
                    AnimalType.Chipmunk -> RandomInt(chipmunkYearsFrom.i..chipmunkYearsTo.i)
                    AnimalType.Badger -> RandomInt(badgerYearsFrom.i..badgerYearsTo.i)
                    AnimalType.FlyingSquirrel -> RandomInt(flyingYearsFrom.i..flyingYearsTo.i)
                    AnimalType.Woodpecker -> RandomInt(woodpeckerYearsFrom.i..woodpeckerYearsTo.i)
                    
                    AnimalType.Kite -> RandomInt(20..30)
                    AnimalType.Wolf -> RandomInt(30..40)
                }
            }
            randoms.treeRandoms.animalRandoms.animalTypeForHole = RandomEnum.createFromEnum(
                mapOf(
                    AnimalType.Badger to badgerStartAmount.i,
                    AnimalType.Chipmunk to chipmunkStartAmount.i,
                    
                    AnimalType.Wolf to 4
                    // others to 0
                )
            )
            randoms.treeRandoms.animalRandoms.animalTypeForHollow = RandomEnum.createFromEnum(
                mapOf(
                    AnimalType.Squirrel to squirrelStartAmount.i,
                    AnimalType.FlyingSquirrel to flyingStartAmount.i,
                    AnimalType.Woodpecker to woodpeckerStartAmount.i,
                    
                    AnimalType.Kite to 1
                    // others to 0
                )
            )
            logArea.appendText("*")  // Debug
        } catch (e: Exception) {
            logArea.appendText("Какая-то ошибка парсинга!\n")
        }
    }
    //</editor-fold>
    
    private fun resetText() {
        logArea.text = "Лес создан\n"
    }
    
    private fun reset() {
        resetText()
        
        Updater.resetAll()
        forest = Forest(3, 3, randoms)
        Updater.addUpdatableToBeginning(Updatable.createFastUpdatable { forest.printAllStatistics() })
    }
    
    
    private fun tick() {
        Updater.tick()
        logArea.appendText(appendingLogPrinter?.getUpdates() ?: "")
    }
}

class ForestViewApp : App(ForestView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = true
        stage.isMaximized = true
        super.start(stage)
    }
}


fun main(args: Array<String>) {
    
    // setup special graphics log.
    idea.forest.log = AppendingLogPrinter()
    
    
    Application.launch(ForestViewApp::class.java, *args)
}
