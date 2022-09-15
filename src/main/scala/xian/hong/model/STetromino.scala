package xian.hong.model
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class STetromino(val color: Color) extends Tetromino{
    var shape: ListBuffer[ListBuffer[Int]] = ListBuffer(ListBuffer(0,1,1), ListBuffer(1,1,0))

    def copy(): Tetromino = {
        val copy = new STetromino(color)
        copy.shape = shape
        copy.X = X
        copy.Y = Y
        copy
    }
}