import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.random.Random

class GameOfLife(val width: Int, val height: Int, val output: MutableList<MutableList<Rectangle>>?, val threadNum: Int) {

    private val empty = 0
    private val full = 1
    private var matrix = mutableListOf<MutableList<Int>>()

    /**
     * Generates initial game of life matrix with provided probability that a cell will be alive
     */
    fun generateMatrix(probability: Double){
        for (i in 0 until height){
            matrix.add(mutableListOf())
            for (j in 0 until width){
                val rnd = Random.nextDouble()
                if (rnd > probability) {
                    matrix[i].add(full)
                } else {
                    matrix[i].add(empty)
                }
            }
        }
    }

    /**
     * Checks if this cell is still within working space of a grid
     */
    private fun isOutOfBounds(value: Int, maximum: Int): Boolean{
        if(value<0 || value>=maximum){
            return true
        }
        return false
    }

    /**
     * Determine whether cell will live on, become alive or die
     */
    private fun getNewCellState(cell: Int, neighCount: Int): Int{
        if(cell == empty){
            if (neighCount == 3){
                return full
            } else {
                return empty
            }
        } else {
            if (neighCount == 2||neighCount == 3){
                return full
            }
            else {
                return empty
            }
        }
    }

    fun runGameOfLifeSerial(infinity: Boolean){
        do {
            val start = System.currentTimeMillis()
            val newMatrix = MutableList(height){ MutableList(width){0}}
            for ((index, row) in matrix.withIndex()) {
                for ((jIndex, column) in row.withIndex()) {
                    var neighCount = 0
                    var i = index - 1
                    var j = jIndex - 1
                    for (x in 0 until 3) {

                        if (isOutOfBounds(i + x, height)) {
                            continue
                        }
                        for (y in 0 until 3) {
                            if (x==1 && y == 1) {
                                continue
                            }
                            if (isOutOfBounds(j + y, width)) {
                                continue
                            }
                            neighCount += matrix[i + x][j + y]
                        }
                    }
                    //println(neighCount)
                    newMatrix[index][jIndex] = getNewCellState(matrix[index][jIndex], neighCount)
                }
            }
            matrix = newMatrix
            drawPopulation()
            println("Elapsed" + (System.currentTimeMillis() - start))
        }while (infinity)
    }

    fun runGameOfLifeThreads(infinity:Boolean){
        var start = System.currentTimeMillis()
        val threads = mutableListOf<Thread>()
        do {
            threads.clear()
            start = System.currentTimeMillis()
            val newMatrix = MutableList(height){ MutableList(width){0}}
            //Launch n threads and assign part of the grid to each of them
            repeat(threadNum) {
                val chunkNum = it
                threads.add(Thread {
                    val subIndex = (height / threadNum).toInt()
                    val end = if (chunkNum == threadNum - 1) {
                        matrix.size
                    } else {
                        subIndex * (chunkNum + 1)
                    }
                    for (baseI in subIndex * chunkNum until end) {
                        for ((baseJ, row) in matrix[subIndex].withIndex()) {
                            var neighCount = 0
                            //val baseI = subIndex
                            val i = baseI - 1
                            val j = baseJ - 1
                            for (x in 0 until 3) {
                                if (isOutOfBounds(i + x, height)) {
                                    continue
                                }
                                for (y in 0 until 3) {
                                    if (x == 1 && y == 1) {
                                        continue
                                    }
                                    if (isOutOfBounds(j + y, width)) {
                                        continue
                                    }
                                    neighCount += matrix[i + x][j + y]
                                }
                            }
                            newMatrix[baseI][baseJ] = getNewCellState(matrix[baseI][baseJ], neighCount)
                        }
                    }
                })
                threads[chunkNum].start()
            }
            for (thread in threads) {
                thread.join()
            }

            matrix = newMatrix
            drawPopulation()
            println("Elapsed" + (System.currentTimeMillis() - start))
        }while (infinity)
    }

    /**
     * Draws population into prepared grid if GUI is available
     */
    private fun drawPopulation(){
        if (output != null) {
            for ((i, row) in matrix.withIndex()) {
                for ((j, col) in row.withIndex()) {
                    if (col == full) {
                        output[i][j].fill = Color.BLACK
                    } else {
                        output[i][j].fill = Color.WHITE
                    }
                }
            }
        } else return
    }
}