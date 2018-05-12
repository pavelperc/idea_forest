package idea.forest.ui

import idea.forest.Forest
import idea.forest.ForestRandoms
import idea.forest.Updater
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import tornadofx.*

class ForestView : View("Idea Forest") {
    override val root: BorderPane by fxml()

    val reset: Button by fxid()
    val stepDelay: TextField by fxid()
    val stepCount: TextField by fxid()
    val run: Button by fxid()

    val logArea: TextArea by fxid()

    var forest: Forest = Forest(3, 3, ForestRandoms())

    init {
        updateText()
        reset.onAction = EventHandler { reset() }
        run.onAction = EventHandler { run() }
    }

    private fun updateText() {
        logArea.text = "Лес создан\n"
    }

    private fun reset() {
        forest = Forest(3, 3, ForestRandoms())
        // TODO: Updater?
    }

    private fun run() {
        val delay = stepDelay.text.toLong()
        val n = stepCount.text.toInt()
        for (_i in 1..n) {
            Updater.start(n, delay)
            logArea.appendText(ForestLogPrinter.getUpdates())
        }
    }
}

class ForestViewApp : App(ForestView::class) {
    override fun start(stage: Stage) {
        stage.isResizable = false
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    Application.launch(ForestViewApp::class.java, *args)
}