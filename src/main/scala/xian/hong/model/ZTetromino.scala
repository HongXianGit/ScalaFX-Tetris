package xian.hong.model
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class ZTetromino(val color: Color) extends Tetromino{
    var shape: ListBuffer[ListBuffer[Int]] = ListBuffer(ListBuffer(1,1,0), ListBuffer(0,1,1))

    def copy(): Tetromino = {
        val copy = new ZTetromino(color)
        copy.shape = shape
        copy.X = X
        copy.Y = Y
        copy
    }
}