import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.stage.Stage
import javafx.scene.shape.Rectangle
import javafx.animation.Animation
import javafx.event.ActionEvent
import javafx.event.EventHandler
import java.time.Clock.tick
import javafx.util.Duration.millis
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration


fun main(args: Array<String>) {
    //Application.launch(Main::class.java,*args)
    val size = 5000
    val gameOfLife = GameOfLife(size,size,null)
    gameOfLife.generateMatrix()
    gameOfLife.runGameOfLifeThreads(true)
}

class Main() : Application() {

    var gameOfLife: GameOfLife? = null
    var action: Timeline? = null

    override fun start(primaryStage: Stage?) {
        val size = 100
        val gridX = size
        val gridY = size
        val recSize = 10.0
        val grid = mutableListOf<MutableList<Rectangle>>()
        var isRunning = false

        val basePane = AnchorPane()
        val btnStart = Button("Start game")
        basePane.children.add(btnStart);
        AnchorPane.setTopAnchor(btnStart, 1.0)
        AnchorPane.setLeftAnchor(btnStart, 1.0)
        AnchorPane.setRightAnchor(btnStart, 1.0)

        btnStart.onAction = object : EventHandler<ActionEvent> {

            override fun handle(event: ActionEvent) {
                if (action?.status == Animation.Status.RUNNING) {
                    action?.stop();
                    btnStart.setText("Start game");
                }else {
                    action?.play();
                    btnStart.setText("Stop game");
                }
            }
        }

        val root = Pane()
        basePane.children.add(root)
        AnchorPane.setBottomAnchor(root,1.0)
        AnchorPane.setLeftAnchor(root,1.0)
        AnchorPane.setRightAnchor(root,1.0)
        AnchorPane.setTopAnchor(root,30.0)

        val rectSize = Bindings.min(root.heightProperty().divide(gridY), root.widthProperty().divide(gridX))

        for (x in 0..gridX){
            grid.add(mutableListOf())
            for (y in 0..gridY){
                val rectangle = Rectangle(recSize,recSize)
                //rectangle.setStroke(Color.WHITE)
                rectangle.strokeWidth = 0.0
                /*rectangle.x = recSize * x
                rectangle.y = recSize * y*/
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
            private val counter: Int = 0 //Canvas, Image, Timer
            override fun handle(event: ActionEvent) {
                /*counter++;
                Point end = snake.get(snake.size()-1);
                Point next = new Point((end.x+direction.x+grid_x)%grid_x,(end.y+direction.y+grid_y)%grid_y);
                snake.add(next);
                grid[next.x][next.y].setFill(Color.RED);
                if(counter%3==0){
                    counter=0;
                    Point start = snake.get(0);
                    grid[start.x][start.y].setFill(Color.BLACK);
                    snake.remove(0);
                }*/

                /*grid[point.x][point.y].setFill(Color.BLACK);
                point.x = (point.x+direction.x+grid_x)%grid_x;
                point.y = (point.y+direction.y+grid_y)%grid_y;
                grid[point.x][point.y].setFill(Color.RED);*/
                gameOfLife?.runGameOfLifeThreads(false)
            }
        }))

        action?.cycleCount = Timeline.INDEFINITE;

        primaryStage?.scene = scene
        primaryStage?.show()
        gameOfLife = GameOfLife(gridX,gridY,grid)
        gameOfLife?.generateMatrix()

        /**/
    }



    /*companion object {
        @JvmStatic
        fun main() {
            launch(Main::class.java)
        }
    }*/

}