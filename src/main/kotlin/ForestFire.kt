import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.random.Random

// treeprob = 0.05
// burnprob = 0.001
class ForestFire(val width: Int, val height: Int, val output: MutableList<MutableList<Rectangle>>?, val threadNum: Int, val burnProb: Double, val treeProb: Double) {
    private val empty = 0
    private val tree = 1
    private val burning = 2
    private var matrix = mutableListOf<MutableList<Int>>()

    /**
     * Generates initial forest fire matrix with provided probability that a cell will be a tree
     */
    fun generateMatrix(probability: Double){
        for (i in 0 until height){
            matrix.add(mutableListOf())
            for (j in 0 until width){
                val rnd = Random.nextDouble()
                if (rnd < probability) {
                    matrix[i].add(tree)
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
            val rnd = Random.nextDouble()
            if (rnd < treeProb){
                return tree
            } else {
                return empty
            }

        } else if (cell == tree){
            if (neighCount >= 1){
                return burning
            }
            val rnd = Random.nextDouble()
            if (rnd < burnProb){
                return burning
            }
            return tree
        } else {
            return empty
        }
    }

    fun runForestFireThreads(infinity:Boolean){
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
                                    neighCount += if (matrix[i + x][j + y] == burning) 1 else 0
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

    private fun drawPopulation(){
        if (output != null) {
            for ((i, row) in matrix.withIndex()) {
                for ((j, col) in row.withIndex()) {
                    if (col == tree) {
                        output[i][j].fill = Color.GREEN
                    } else if (col==burning){
                        output[i][j].fill = Color.ORANGE
                    } else {
                        output[i][j].fill = Color.WHITE
                    }
                }
            }
        } else return
    }
}