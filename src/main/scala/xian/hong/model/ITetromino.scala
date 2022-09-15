package xian.hong.model 
import scalafx.scene.paint.Color
import scala.collection.mutable.ListBuffer

class ITetromino(val color: Color) extends Tetromino{
    var shape: ListBuffer[ListBuffer[Int]] = ListBuffer(ListBuffer(1,1,1,1))

    //override superclass rotate method to improve the visual experience
    override def rotate(): Unit = {
        super.rotate()
        if(width==1){
            X = X + 1
            Y = Y - 1
        }else{
            X = X - 1
            Y = Y + 1
        }
    }


    def copy(): Tetromino = {
        val copy = new ITetromino(color)
        copy.shape = shape
        copy.X = X
        copy.Y = Y
        copy
    }
}
