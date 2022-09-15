package xian.hong.model 
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class OTetromino(val color: Color) extends Tetromino{
    var shape: ListBuffer[ListBuffer[Int]] = ListBuffer(ListBuffer(1,1), ListBuffer(1,1))

    def copy(): Tetromino = {
        val copy = new OTetromino(color)
        copy.shape = shape
        copy.X = X
        copy.Y = Y
        copy
    }
}