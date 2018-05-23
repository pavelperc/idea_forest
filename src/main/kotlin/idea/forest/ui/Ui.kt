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
    private val TextField.i get() = text.trim().toIntOrNull()
    private val TextField.d get() = text.trim().toDoubleOrNull()
    
    //<editor-fold defaultstate="collapsed" desc="UI objects">
    override val root: BorderPane by fxml()
    
    private val firStartAmount: TextField by fxid()
    private val pineStartAmount: TextField by fxid()
    private val oakStartAmount: TextField by fxid()
    private val birchStartAmount: TextField by fxid()
    private val mapleStartAmount: TextField by fxid()
    private val walnutStartAmount: TextField by fxid()
    
    private val squirrelStartAmount: TextField by fxid()
    private val squirrelFrom: TextField by fxid()
    private val squirrelTo: TextField by fxid()
    private val squirrelYearsFrom: TextField by fxid()
    private val squirrelYearsTo: TextField by fxid()
    private val chipmunkStartAmount: TextField by fxid()
    private val chipmunkFrom: TextField by fxid()
    private val chipmunkTo: TextField by fxid()
    private val chipmunkYearsFrom: TextField by fxid()
    private val chipmunkYearsTo: TextField by fxid()
    private val badgerStartAmount: TextField by fxid()
    private val badgerFrom: TextField by fxid()
    private val badgerTo: TextField by fxid()
    private val badgerYearsFrom: TextField by fxid()
    private val badgerYearsTo: TextField by fxid()
    private val flyingStartAmount: TextField by fxid()
    private val flyingFrom: TextField by fxid()
    private val flyingTo: TextField by fxid()
    private val flyingYearsFrom: TextField by fxid()
    private val flyingYearsTo: TextField by fxid()
    private val woodpeckerStartAmount: TextField by fxid()
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
    private val TextField.on: Unit get() {
        this.onKeyPressed = EventHandler { updateSettings() }
    }

    private fun addListeners() {
        reset.onAction = EventHandler { reset() }
        tick.onAction = EventHandler { tick() }
        
        firStartAmount.on
        pineStartAmount.on
        oakStartAmount.on
        birchStartAmount.on
        mapleStartAmount.on
        walnutStartAmount.on
        
        squirrelStartAmount.on
        squirrelFrom.on
        squirrelTo.on
        squirrelYearsFrom.on
        squirrelYearsTo.on
        chipmunkStartAmount.on
        chipmunkFrom.on
        chipmunkTo.on
        chipmunkYearsFrom.on
        chipmunkYearsTo.on
        badgerStartAmount.on
        badgerFrom.on
        badgerTo.on
        badgerYearsFrom.on
        badgerYearsTo.on
        flyingStartAmount.on
        flyingFrom.on
        flyingTo.on
        flyingYearsFrom.on
        flyingYearsTo.on
        woodpeckerStartAmount.on
        woodpeckerFrom.on
        woodpeckerTo.on
        woodpeckerYearsFrom.on
        woodpeckerYearsTo.on
    }
    //</editor-fold>

    private fun updateSettings() {
        updateTreeSettings()
        updateAnimalSettings()

        logArea.appendText("*")
    }

    //<editor-fold defaultstate="collapsed" desc="settings updater functions">
    private fun updateTreeSettings() {
        run /* Update tree start fraction */ {
            val fir = firStartAmount.i ?: randoms.randomTreeType.freqs[0]
            val pine = pineStartAmount.i ?: randoms.randomTreeType.freqs[1]
            val oak = oakStartAmount.i ?: randoms.randomTreeType.freqs[2]
            val birch = birchStartAmount.i ?: randoms.randomTreeType.freqs[3]
            val maple = mapleStartAmount.i ?: randoms.randomTreeType.freqs[4]
            val walnut = walnutStartAmount.i ?: randoms.randomTreeType.freqs[5]

            /** setup frequencies for FIR, PINE, OAK, BIRCH, MAPLE, WALNUT */
            randoms.randomTreeType = RandomEnum(intArrayOf(fir, pine, oak, birch, maple, walnut))
        }
    }

    private fun updateAnimalSettings() {
        run /* Update animal max age */ {
            val target = randoms.treeRandoms.animalRandoms.maxAge

            val squirrel1 = squirrelFrom.i ?: target(AnimalType.Squirrel).range.start
            val squirrel2 = squirrelTo.i ?: target(AnimalType.Squirrel).range.endInclusive
            val chipmunk1 = chipmunkFrom.i ?: target(AnimalType.Chipmunk).range.start
            val chipmunk2 = chipmunkTo.i ?: target(AnimalType.Chipmunk).range.endInclusive
            val badger1 = badgerFrom.i ?: target(AnimalType.Badger).range.start
            val badger2 = badgerTo.i ?: target(AnimalType.Badger).range.endInclusive
            val flying1 = flyingFrom.i ?: target(AnimalType.FlyingSquirrel).range.start
            val flying2 = flyingTo.i ?: target(AnimalType.FlyingSquirrel).range.endInclusive
            val woodpecker1 = woodpeckerFrom.i ?: target(AnimalType.Woodpecker).range.start
            val woodpecker2 = woodpeckerTo.i ?: target(AnimalType.Woodpecker).range.endInclusive

            randoms.treeRandoms.animalRandoms.maxAge = {
                when (it) {
                    AnimalType.Squirrel -> RandomInt(squirrel1..squirrel2)
                    AnimalType.Chipmunk -> RandomInt(chipmunk1..chipmunk2)
                    AnimalType.Badger -> RandomInt(badger1..badger2)
                    AnimalType.FlyingSquirrel -> RandomInt(flying1..flying2)
                    AnimalType.Woodpecker -> RandomInt(woodpecker1..woodpecker2)

                    AnimalType.Kite -> RandomInt(20..30)  // TODO: provide support for predators
                    AnimalType.Wolf -> RandomInt(30..40)
                }
            }
        }

        run /* Update animal birth count */ {
            val target = randoms.treeRandoms.animalRandoms.birthCount

            val squirrel1 = squirrelFrom.i ?: target(AnimalType.Squirrel).range.start
            val squirrel2 = squirrelTo.i ?: target(AnimalType.Squirrel).range.endInclusive
            val chipmunk1 = chipmunkFrom.i ?: target(AnimalType.Chipmunk).range.start
            val chipmunk2 = chipmunkTo.i ?: target(AnimalType.Chipmunk).range.endInclusive
            val badger1 = badgerFrom.i ?: target(AnimalType.Badger).range.start
            val badger2 = badgerTo.i ?: target(AnimalType.Badger).range.endInclusive
            val flying1 = flyingFrom.i ?: target(AnimalType.FlyingSquirrel).range.start
            val flying2 = flyingTo.i ?: target(AnimalType.FlyingSquirrel).range.endInclusive
            val woodpecker1 = woodpeckerFrom.i ?: target(AnimalType.Woodpecker).range.start
            val woodpecker2 = woodpeckerTo.i ?: target(AnimalType.Woodpecker).range.endInclusive

            randoms.treeRandoms.animalRandoms.birthCount = {
                when (it) {
                    AnimalType.Squirrel -> RandomInt(squirrel1..squirrel2)
                    AnimalType.Chipmunk -> RandomInt(chipmunk1..chipmunk2)
                    AnimalType.Badger -> RandomInt(badger1..badger2)
                    AnimalType.FlyingSquirrel -> RandomInt(flying1..flying2)
                    AnimalType.Woodpecker -> RandomInt(woodpecker1..woodpecker2)

                    AnimalType.Kite -> RandomInt(0..2)  // TODO: provide support for predators
                    AnimalType.Wolf -> RandomInt(0..2)
                }
            }
        }

        run /* Update holes */ {
            val target = randoms.treeRandoms.animalRandoms.animalTypeForHole.freqs
            val badger = badgerStartAmount.i ?: target[AnimalType.Badger.ordinal]
            val chipmunk = chipmunkStartAmount.i ?: target[AnimalType.Chipmunk.ordinal]

            randoms.treeRandoms.animalRandoms.animalTypeForHole = RandomEnum.createFromEnum(
                    mapOf(
                            AnimalType.Badger to badger,
                            AnimalType.Chipmunk to chipmunk,

                            AnimalType.Wolf to 4  // TODO: provide support for predators
                            // others to 0
                    )
            )
        }

        run /* Update hollows */ {
            val target = randoms.treeRandoms.animalRandoms.animalTypeForHollow.freqs
            val squirrel = squirrelStartAmount.i ?: target[AnimalType.Squirrel.ordinal]
            val flying = flyingStartAmount.i ?: target[AnimalType.FlyingSquirrel.ordinal]
            val woodpecker = woodpeckerStartAmount.i ?: target[AnimalType.Woodpecker.ordinal]

            randoms.treeRandoms.animalRandoms.animalTypeForHollow = RandomEnum.createFromEnum(
                    mapOf(
                            AnimalType.Squirrel to squirrel,
                            AnimalType.FlyingSquirrel to flying,
                            AnimalType.Woodpecker to woodpecker,

                            AnimalType.Kite to 1  // TODO: provide support for predators
                            // others to 0
                    )
            )
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
