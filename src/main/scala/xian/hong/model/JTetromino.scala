package xian.hong.model 
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class JTetromino(val color: Color) extends Tetromino{
    var shape: ListBuffer[ListBuffer[Int]] = ListBuffer(ListBuffer(0,1), ListBuffer(0,1), ListBuffer(1,1))

    def copy(): Tetromino = {
        val copy = new JTetromino(color)
        copy.shape = shape
        copy.X = X
        copy.Y = Y
        copy
    }
}