package xian.hong.model
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

abstract class Tetromino(){
    //color of the block
    val color: Color

    //the shape of the block
    var shape: ListBuffer[ListBuffer[Int]]

    //x-coordinate of the block in the game area
    var X: Int = 0

    //y-coordinate of the block in the game area
    var Y: Int = 0

    //height of the block
    def height: Int = shape.length
    
    //width of the block
    def width: Int = shape(0).length

    //initialize the spawn coordinate of the block
    def spawnCoordinate(x: Int): Unit = {
        X = x
        Y = -height
    } 

    def moveDown(): Unit = {
        Y += 1
    }

    def moveLeft(): Unit = {
        X -= 1
    }

    def moveRight(): Unit = {
        X += 1
    }

    //rotate the block
    def rotate(): Unit = {
        val newShape: ListBuffer[ListBuffer[Int]] = ListBuffer()
        var colBuffer: ListBuffer[Int] = ListBuffer()
        for(col <- 0 until width){
            for(row <- height - 1 to 0 by -1){
                colBuffer += shape(row)(col)
            }
            newShape += colBuffer
            colBuffer = ListBuffer()
        }
        shape = newShape
    }

    //copy the block with the same properties for rotate checking
    def copy(): Tetromino
}
