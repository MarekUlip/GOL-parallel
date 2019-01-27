import com.sun.org.apache.xpath.internal.operations.Bool
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import kotlin.random.Random
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger

class GameOfLife(val width: Int, val height: Int, val output: MutableList<MutableList<Rectangle>>?) {

    val empty = 0
    val full = 1
    var matrix = mutableListOf<MutableList<Int>>()

    fun generateMatrix(){
        for (i in 0..height){
            matrix.add(mutableListOf<Int>())
            for (j in 0..width){
                val rnd = Random.nextInt()
                if (rnd > 0.5) {
                    matrix[i].add(full)
                } else {
                    matrix[i].add(empty)
                }
            }
        }
    }

    fun isOutOfBounds(value: Int, maximum: Int): Boolean{
        if(value<0 || value>=maximum){
            return true
        }
        return false
    }

    fun getNewCellState(cell: Int, neighCount: Int): Int{
        if(cell == empty){
            if (neighCount == 3){
                return full
            } else {
                return empty
            }
        } else {
            if (neighCount < 2){
                return empty
            } else if (neighCount == 2||neighCount == 3){
                return full
            } else {
                return empty
            }
        }
    }

    fun runGameOfLifeSerial(infinity: Boolean){
        do {
            val start = System.currentTimeMillis()
            val newMatrix = matrix.toMutableList()
            for ((index, row) in matrix.withIndex()) {
                for ((jIndex, column) in row.withIndex()) {
                    var neighCount = 0
                    var i = index - 1
                    var j = jIndex - 1
                    for (x in 0..3) {

                        if (isOutOfBounds(i + x, height)) {
                            continue
                        }
                        for (y in 0..3) {
                            if (i + x == j + y) {
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
            matrix = newMatrix.toMutableList()
            drawPopulation()
            println("Elapsed" + (System.currentTimeMillis() - start))
        }while (infinity)
    }

    fun runGameOfLifeThreads(infinity:Boolean){
        var start = System.currentTimeMillis()
        val index = AtomicInteger(0)
        val jIndex = AtomicInteger(0)
        val threadNum = 6
        val threads = mutableListOf<Thread>()

            do {
                threads.clear()
                start = System.currentTimeMillis()
                index.set(0)
                val newMatrix = matrix.toMutableList()

                    repeat(threadNum) {
                        //launch {
                        val chunkNum = it
                        threads.add(Thread {
                            val subIndex = (height / threadNum).toInt()///index.getAndIncrement()
                            val end = if (chunkNum == threadNum - 1) {
                                matrix.size - 1
                            } else {
                                subIndex * (chunkNum + 1)
                            }
                            for (baseI in subIndex * chunkNum..end) {
                                for ((baseJ, row) in matrix[subIndex].withIndex()) {
                                    var neighCount = 0
                                    //val baseI = subIndex
                                    val i = baseI - 1
                                    val j = baseJ - 1
                                    for (x in 0..3) {

                                        if (isOutOfBounds(i + x, height)) {
                                            continue
                                        }
                                        for (y in 0..3) {
                                            if (i + x == j + y) {
                                                continue
                                            }
                                            if (isOutOfBounds(j + y, width)) {
                                                continue
                                            }
                                            neighCount += matrix[i + x][j + y]
                                        }
                                    }
                                    //println(neighCount)
                                    newMatrix[baseI][baseJ] = getNewCellState(matrix[baseI][baseJ], neighCount)
                                }
                                //}
                            }
                        })
                        threads[chunkNum].start()
                    }
                for (thread in threads) {
                    thread.join()
                }
                matrix = newMatrix.toMutableList()
                drawPopulation()
                println("Elapsed" + (System.currentTimeMillis() - start))
            }while (infinity)
    }


    fun runGameOfLifeConstrained(infinity: Boolean) {
        //while (true) {
        //val coroutines = mutableListOf<Job>()
        var start = System.currentTimeMillis()
        val index = AtomicInteger(0)
        val jIndex = AtomicInteger(0)
        val threadNum = 8
        runBlocking {
            do {
                start = System.currentTimeMillis()
                index.set(0)
                val newMatrix = matrix.toMutableList()
                coroutineScope {
                    repeat(threadNum) {
                        //launch {
                        val chunkNum = it
                        val subIndex = (height / threadNum).toInt()///index.getAndIncrement()
                        val end = if (chunkNum == threadNum-1){
                            matrix.size-1
                        } else {
                            subIndex * (chunkNum + 1)
                        }
                        for (baseI in subIndex * chunkNum..end) {
                            for ((baseJ, row) in matrix[subIndex].withIndex()) {
                                var neighCount = 0
                                //val baseI = subIndex
                                val i = baseI - 1
                                val j = baseJ - 1
                                for (x in 0..3) {

                                    if (isOutOfBounds(i + x, height)) {
                                        continue
                                    }
                                    for (y in 0..3) {
                                        if (i + x == j + y) {
                                            continue
                                        }
                                        if (isOutOfBounds(j + y, width)) {
                                            continue
                                        }
                                        neighCount += matrix[i + x][j + y]
                                    }
                                }
                                //println(neighCount)
                                newMatrix[baseI][baseJ] = getNewCellState(matrix[baseI][baseJ], neighCount)
                            }
                            //}
                        }
                    }
                }
                matrix = newMatrix.toMutableList()
                drawPopulation()
                println("Elapsed" + (System.currentTimeMillis() - start))
            }while (infinity)
        }
    }

    fun runGameOfLife(infinity: Boolean) {
        //while (true) {
        //val coroutines = mutableListOf<Job>()
        var start = System.currentTimeMillis()
        val index = AtomicInteger(0)
        val jIndex = AtomicInteger(0)
        runBlocking {
            do{
                start = System.currentTimeMillis()
                index.set(0)
            val newMatrix = matrix.toMutableList()
                coroutineScope {
                    List(width) {

                        launch {
                            val subIndex = index.getAndIncrement()
                            for ((baseJ, row) in matrix[subIndex].withIndex()) {
                                var neighCount = 0
                                val baseI = subIndex
                                val i = baseI - 1
                                val j = baseJ - 1
                                for (x in 0..3) {

                                    if (isOutOfBounds(i + x, height)) {
                                        continue
                                    }
                                    for (y in 0..3) {
                                        if (i + x == j + y) {
                                            continue
                                        }
                                        if (isOutOfBounds(j + y, width)) {
                                            continue
                                        }
                                        neighCount += matrix[i + x][j + y]
                                    }
                                }
                                //println(neighCount)
                                newMatrix[baseI][baseJ] = getNewCellState(matrix[baseI][baseJ], neighCount)
                            }
                        }
                    }
                }
                matrix = newMatrix.toMutableList()
                drawPopulation()
                println("Elapsed" + (System.currentTimeMillis()-start))
            }while (infinity)
            }

            //println("\u001Bc")


        /*runBlocking {
            val newMatrix = matrix.toMutableList()
            for ((index, row) in matrix.withIndex()) {
                //coroutines.add(launch {
                launch {
                    for ((jIndex, column) in row.withIndex()) {
                        var neighCount = 0
                        var i = index - 1
                        var j = jIndex - 1
                        for (x in 0..3) {

                            if (isOutOfBounds(i + x, height)) {
                                continue
                            }
                            for (y in 0..3) {
                                if (i + x == j + y) {
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
                }}//)
            }
            /*for (coroutine in coroutines){
                coroutine.join()
            }*/
            matrix = newMatrix.toMutableList()
            //println("\u001Bc")
            drawPopulation()
        }*/
        println("Elapsed" + (System.currentTimeMillis()-start))
        //}

        /*val newMatrix = matrix.toMutableList()
        for ((index, row) in matrix.withIndex()) {
            for ((jIndex, column) in row.withIndex()) {
                var neighCount = 0
                var i = index - 1
                var j = jIndex - 1
                for (x in 0..3) {

                    if (isOutOfBounds(i + x, height)) {
                        continue
                    }
                    for (y in 0..3) {
                        if (i + x == j + y) {
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
        }*/
    }
    fun drawPopulation(){
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