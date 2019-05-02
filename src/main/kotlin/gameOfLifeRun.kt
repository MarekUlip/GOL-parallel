import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.scene.shape.Rectangle
import javafx.animation.Animation
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration


fun main(args: Array<String>) {
    val useGUI = false
    val testGameOfLife = false
    if (useGUI) {
        Application.launch(Main::class.java,*args)
    } else {
        val size = 5000
        if (testGameOfLife){
        val gameOfLife = GameOfLife(size, size, null, 6)
        gameOfLife.generateMatrix(0.5)
        gameOfLife.runGameOfLifeThreads(true)}
        else{
            val forestFire = ForestFire(size, size, null, 6,0.001,0.05)
            forestFire.generateMatrix(0.8)
            forestFire.runForestFireThreads(true)
        }
    }
}

class Main : Application() {

    var gameOfLife: GameOfLife? = null
    var forestFire: ForestFire? = null
    var action: Timeline? = null
    var actionFF: Timeline? = null

    override fun start(primaryStage: Stage?) {
        val size = 100
        val gridX = size
        val gridY = size
        val recSize = 10.0
        val grid = mutableListOf<MutableList<Rectangle>>()

        val basePane = AnchorPane()
        val btnStartGol = Button("Start game of life")
        val btnStartFF = Button("Start Forest Fire")
        basePane.children.add(btnStartGol)
        AnchorPane.setTopAnchor(btnStartGol, 1.0)
        AnchorPane.setLeftAnchor(btnStartGol, 1.0)
        AnchorPane.setRightAnchor(btnStartGol, 1.0)
        basePane.children.add(btnStartFF)
        AnchorPane.setTopAnchor(btnStartFF, 30.0)
        AnchorPane.setLeftAnchor(btnStartFF, 1.0)
        AnchorPane.setRightAnchor(btnStartFF, 1.0)


        btnStartGol.onAction = object : EventHandler<ActionEvent> {

            override fun handle(event: ActionEvent) {
                if (action?.status == Animation.Status.RUNNING) {
                    action?.stop()
                    btnStartGol.text = "Start game of life"
                }else {
                    action?.play()
                    btnStartGol.text = "Stop game of life"
                }
            }
        }

        btnStartFF.onAction = object : EventHandler<ActionEvent> {

            override fun handle(event: ActionEvent) {
                if (actionFF?.status == Animation.Status.RUNNING) {
                    actionFF?.stop()
                    btnStartFF.text = "Start Forest Fire"
                }else {
                    actionFF?.play()
                    btnStartFF.text = "Stop Forest Fire"
                }
            }
        }

        val root = Pane()
        basePane.children.add(root)
        AnchorPane.setBottomAnchor(root,1.0)
        AnchorPane.setLeftAnchor(root,1.0)
        AnchorPane.setRightAnchor(root,1.0)
        AnchorPane.setTopAnchor(root,60.0)

        val rectSize = Bindings.min(root.heightProperty().divide(gridY), root.widthProperty().divide(gridX))

        for (x in 0 until gridX){
            grid.add(mutableListOf())
            for (y in 0 until gridY){
                val rectangle = Rectangle(recSize,recSize)
                rectangle.strokeWidth = 0.0
                rectangle.fill = Color.RED
                rectangle.xProperty().bind(rectSize.multiply(x))
                rectangle.yProperty().bind(rectSize.multiply(y))
                rectangle.heightProperty().bind(rectSize)
                rectangle.widthProperty().bind(rectangle.heightProperty())
                grid[x].add(rectangle)
                root.children.add(grid[x][y])
            }
        }
        val scene = Scene(basePane, 800.0,600.0)

        action = Timeline(KeyFrame(Duration.millis(100.0), object : EventHandler<ActionEvent> {
            override fun handle(event: ActionEvent) {
                gameOfLife?.runGameOfLifeThreads(false)
            }
        }))

        action?.cycleCount = Timeline.INDEFINITE

        actionFF = Timeline(KeyFrame(Duration.millis(100.0), object : EventHandler<ActionEvent> {
            override fun handle(event: ActionEvent) {
                forestFire?.runForestFireThreads(false)
            }
        }))

        actionFF?.cycleCount = Timeline.INDEFINITE

        primaryStage?.scene = scene
        primaryStage?.show()
        gameOfLife = GameOfLife(gridX,gridY,grid,6)
        gameOfLife?.generateMatrix(0.5)
        forestFire = ForestFire(gridX, gridY, grid,6,0.001,0.05)
        forestFire?.generateMatrix(0.8)

    }




}