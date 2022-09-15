package xian.hong.view

import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.scene.layout.{GridPane,AnchorPane,ColumnConstraints,RowConstraints}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.ObjectProperty
import scalafx.event.ActionEvent
import scalafx.animation.{Timeline,KeyFrame}
import scalafx.util.Duration
import scalafx.scene.paint.Color
import scalafx.collections.ObservableBuffer
import scalafx.scene.Node
import scalafx.scene.shape.{Shape,Rectangle,StrokeType}
import xian.hong.model._
import scalafx.scene.input.{KeyEvent,KeyCode}
import scala.collection.mutable.ListBuffer
import xian.hong.TetrisScalaFx
import scala.util.Random
import scalafx.scene.media.{Media,MediaPlayer}

@sfxml
class GameController(
    private val gameArea : GridPane,
    private val nextBlockArea: AnchorPane,
    private val scoreLabel: Label,
    private val levelLabel: Label
) {
  //initialize sound effects
  val clearMedia = new Media(getClass.getResource("/sound/clear.wav").toString())
  val clearMediaPlayer = new MediaPlayer(clearMedia)
  clearMediaPlayer.volume = 0.2

  val collisionMedia = new Media(getClass.getResource("/sound/collision.wav").toString())
  val collisionMediaPlayer = new MediaPlayer(collisionMedia)
  collisionMediaPlayer.volume = 0.2

  val gameOverMedia = new Media(getClass.getResource("/sound/gameOver.wav").toString())
  val gameOverMediaPlayer = new MediaPlayer(gameOverMedia)
  gameOverMediaPlayer.volume = 0.2

  val hitBorderMedia = new Media(getClass.getResource("/sound/hitBorder.wav").toString())
  val hitBorderMediaPlayer = new MediaPlayer(hitBorderMedia)
  hitBorderMediaPlayer.volume = 0.2

  val moveMedia = new Media(getClass.getResource("/sound/move.wav").toString())
  val moveMediaPlayer = new MediaPlayer(moveMedia)
  moveMediaPlayer.volume = 0.2

  val rotateMedia = new Media(getClass.getResource("/sound/rotate.wav").toString())
  val rotateMediaPlayer = new MediaPlayer(rotateMedia)
  rotateMediaPlayer.volume = 0.2

  //shapes that can be spawned
  val tetrominoes: List[Tetromino] = List(new ITetromino(Color.rgb(1, 240, 241)), new TTetromino(Color.rgb(160, 0, 243)), new STetromino(Color.rgb(0, 240, 0)), new ZTetromino(Color.rgb(240, 1, 0)), new OTetromino(Color.rgb(240, 240, 1)), new JTetromino(Color.rgb(1, 1, 240)), new LTetromino(Color.rgb(239, 160, 0))) 

  //the variable to store the score
  var score = ObjectProperty[Int](0)

  //the variable to store the level
  var level = ObjectProperty[Int](1)

  //get the rows of the game area
  val rows: Int = gameArea.rowConstraints.size

  //get the columns of the game area
  val columns: Int = gameArea.columnConstraints.size

  //calculate the size of each cell
  val cellSize: Double = 300 / columns

  //a ObservableBuffer to store the scalafx nodes
  var nodesBuffer: ObservableBuffer[Node] = ObservableBuffer()

  //spawn the current block
  var currentBlock: Tetromino = spawnBlock(null)

  //spawn the next block
  var nextBlock: Tetromino = spawnBlock(currentBlock)

  //a list that keeps tracks of the background
  var backgroundList: ListBuffer[ListBuffer[Int]] = ListBuffer()

  //initialize the game area action to null 
  var gameAreaAction: Timeline = null

  //initialize the backgroundList
  resetBackgroundList()

  //update the score label after the score changes
  score.onChange((_, _, newValue) => {scoreLabel.text = s"$newValue"})

  //update the level label after the level changes, speed up the game after increasing level
  level.onChange((_, _, newValue) => {
    if(gameAreaAction.cycleDuration() != Duration(100)){
      levelLabel.text = s"$newValue"
      gameAreaAction.stop()
      gameAreaAction.keyFrames = ObservableBuffer(KeyFrame(Duration(600-(newValue*50)), onFinished = finishAction))
      gameAreaAction.play()
    }
  })

  //action that will be performed every 600ms
  val finishAction = (ae: ActionEvent) => {
    if(checkBottom()){
      moveBlockDown()
      repaintGameArea()
    }else{
      if(!isGameOver){
        collisionMediaPlayer.stop()
        collisionMediaPlayer.play()
        moveBlockToBackground()
        clearLines()
        currentBlock = nextBlock
        nextBlock = spawnBlock(currentBlock)
        repaintGameArea()
        repaintNextBlockArea()
      }else{
        handleGameOver()
      }
    }
  }

  //update the UI every 600ms
  //it will stop only after game over
  gameAreaAction = new Timeline{
    cycleCount = Timeline.Indefinite
    keyFrames += KeyFrame(Duration(600),  onFinished = finishAction)
  }

  //start the game
  gameAreaAction.play()

  //paint the next block area
  repaintNextBlockArea()

  //spawn a new currentBlock
  //current block will be inserted as a argument to prevent getting two same blocks in a row
  def spawnBlock(blockToBeRemoved: Tetromino): Tetromino = {
    val filterTetrominos = tetrominoes.filter(_!=blockToBeRemoved)
    val newBlock = filterTetrominos(Random.nextInt(filterTetrominos.length))
    newBlock.spawnCoordinate(Random.nextInt((columns-newBlock.width)))
    newBlock
  }

  //draw the currentBlock on the gameArea
  def drawBlock(){
    for(r <- 0 until currentBlock.height){
      for(c <- 0 until currentBlock.width){
        if(currentBlock.shape(r)(c) == 1 && r+currentBlock.Y >= 0)
          gameArea.add(new Rectangle{
            width = cellSize
            height = cellSize
            fill = currentBlock.color
            strokeType = StrokeType.Centered
            strokeWidth = 1
            stroke = Color.White
          },c+currentBlock.X,r+currentBlock.Y)
      }
    }
  }

  //draw the next currentBlock
  def drawNextBlock(){
    nextBlockArea.resize(nextBlock.width*cellSize, nextBlock.height*cellSize)
    val nextBlockGP = new GridPane()
    var colConst: ColumnConstraints = null
    var rowConst: ColumnConstraints = null

    //set the grid pane column constraint
    for (column <- 0 until nextBlock.width) {
      colConst = new ColumnConstraints(cellSize)
      nextBlockGP.columnConstraints += colConst
    }

    //set the grid pane row constraint
    for (row <- 0 until nextBlock.height) {
      val rowConst = new RowConstraints(cellSize)
      nextBlockGP.rowConstraints += rowConst
    }

    //draw the block at the grid pane
    for(r <- 0 until nextBlock.height){
      for(c <- 0 until nextBlock.width){
        if(nextBlock.shape(r)(c) == 1)
          nextBlockGP.add(new Rectangle{
            width = cellSize
            height = cellSize
            fill = nextBlock.color
            strokeType = StrokeType.Centered
            strokeWidth = 1
            stroke = Color.White
          },c,r)
      }
    }
    nextBlockArea.children = nextBlockGP
  }

  //move the currentBlock down
  def moveBlockDown(){
    currentBlock.moveDown()
  }

  //move the currentBlock Left
  def moveBlockLeft(){
    if(checkLeft()){
      currentBlock.moveLeft()
      moveMediaPlayer.stop()
      moveMediaPlayer.play()
    }else{
      hitBorderMediaPlayer.stop()
      hitBorderMediaPlayer.play()
    }
  }

  //move the currentBlock right
  def moveBlockRight(){
    if(checkRight()){
      currentBlock.moveRight()
      moveMediaPlayer.stop()
      moveMediaPlayer.play()
    }else{
      hitBorderMediaPlayer.stop()
      hitBorderMediaPlayer.play()
    }
  }

  //rotate the currentBlock down
  def rotateBlock(){
    val result = checkRotate()
    if(result._1){
      currentBlock.rotate()
      currentBlock.X = result._2
      currentBlock.Y = result._3
      rotateMediaPlayer.stop()
      rotateMediaPlayer.play()
    }
  }

  //check whether the currentBlock can move down
  def checkBottom(): Boolean = {
    var moveDownAble = true
    if(currentBlock.Y + currentBlock.height == rows)
      moveDownAble = false
    
    for(row <- currentBlock.Y + currentBlock.height - 1 to currentBlock.Y by -1){
      for(col <- currentBlock.X until currentBlock.X + currentBlock.width){
        nodesBuffer.foreach(node => {
          if(GridPane.getRowIndex(node) == row+1 && GridPane.getColumnIndex(node) == col && currentBlock.shape(row-currentBlock.Y)(col-currentBlock.X) == 1)
            moveDownAble = false
        })
      }
    }
    moveDownAble
  }

  //check whether the currentBlock can move left
  def checkLeft(): Boolean = {
    var moveLeftAble = true
    if(currentBlock.X <= 0)
      moveLeftAble = false
    
    for(col <- currentBlock.X until currentBlock.X + currentBlock.width){
      for(row <- currentBlock.Y until currentBlock.Y + currentBlock.height){
        nodesBuffer.foreach(node => {
          if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col-1 && currentBlock.shape(row-currentBlock.Y)(col-currentBlock.X) == 1)
            moveLeftAble = false
        })
      }
    }
    moveLeftAble
  }

  //check whether the currentBlock can move right
  def checkRight(): Boolean = {
    var moveRightAble = true
    if(currentBlock.X + currentBlock.width >= 10)
      moveRightAble = false
    
    for(col <- currentBlock.X + currentBlock.width - 1 to currentBlock.X by -1){
      for(row <- currentBlock.Y until currentBlock.Y + currentBlock.height){
        nodesBuffer.foreach(node => {
          if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col+1 && currentBlock.shape(row-currentBlock.Y)(col-currentBlock.X) == 1)
            moveRightAble = false
        })
      }
    }
    moveRightAble
  }

  //check whether the currentBlock can rotate
  //clone the current block to test whether it is rotateble
  def checkRotate(): (Boolean,Int,Int) = {
    var rotatable = true
    val currentBlockCopy = currentBlock.copy()
    currentBlockCopy.rotate()

    if(currentBlockCopy.X < 0)
      currentBlockCopy.X = 0

    if(currentBlockCopy.X + currentBlockCopy.width > columns)
      currentBlockCopy.X = columns - currentBlockCopy.width
      
    if(currentBlockCopy.Y + currentBlockCopy.height > rows)
      currentBlockCopy.Y = rows - currentBlockCopy.height

    for(row <- 0 until currentBlockCopy.height){
      for(col <- 0 until currentBlockCopy.width){
        nodesBuffer.foreach(node => {
          if(GridPane.getRowIndex(node) == (currentBlockCopy.Y + row) && GridPane.getColumnIndex(node) == (currentBlockCopy.X + col) && (currentBlockCopy.shape(row)(col) == 1))
            rotatable = false
        })
      }
    }
    (rotatable,currentBlockCopy.X,currentBlockCopy.Y)
  }

  //repaint the game area 
  def repaintGameArea(){
    gameArea.children = nodesBuffer
    drawBlock()
  }

  //repaint the next block area
  def repaintNextBlockArea(){
    drawNextBlock()
  }

  //move the currentBlock to the background after the currentBlock cannot move anymore
  def moveBlockToBackground() = {
    nodesBuffer = ObservableBuffer()
    resetBackgroundList()
    gameArea.children.foreach{x => 
      nodesBuffer += x
      backgroundList(GridPane.getRowIndex(x))(GridPane.getColumnIndex(x)) = 1
    }
  }

  /*
  handle user input 
  up arrow key = rotate
  left arrow key = move left
  right arrow key = move right
  down arrow key = move down
  space bar = hard drop 
  */
  def handlePressed(key: KeyEvent) = {
    if(currentBlock!=null){
      if(key.code == KeyCode.Left){
        moveBlockLeft()
      } else if(key.code == KeyCode.Right){
        moveBlockRight()
      } else if(key.code == KeyCode.Space){
        while(checkBottom())
          moveBlockDown()
      } else if(key.code == KeyCode.Down){
        if(checkBottom())
          moveBlockDown()
      } else if(key.code == KeyCode.Up){
        rotateBlock()
      }
      repaintGameArea()
    }
  }

  //reset the background list to a list with dimension 20x10 and contains all zeros for each element
  def resetBackgroundList(){
    backgroundList = ListBuffer.fill(20, 10)(0)
  }

  // clear the lines that are filled
  def clearLines(){
    var lineFilled = true
    var linesCleared = 0
    var row = backgroundList.length - 1
    while(row >= 0){
      lineFilled = backgroundList(row).forall(_ == 1)
      if(lineFilled){
        clearLine(row)
        clearMediaPlayer.stop()
        clearMediaPlayer.play()
        moveLineDown(row)
        row = row + 1
        linesCleared = linesCleared + 1
      }
      row = row - 1
      lineFilled = true
    }
    score.value = score.value + ((linesCleared*10) * (if(linesCleared > 1) (linesCleared + 1) * 0.5 else 1)).toInt
    updateLevel()
  }

  //update the level every 100 points
  def updateLevel(){
    if((score.value/70) + 1 > level.value)
      level.value = score.value/70 + 1
  }

  //clear the particular line
  def clearLine(row: Int){
    nodesBuffer = nodesBuffer.filter(x => GridPane.getRowIndex(x) != row)
    gameArea.children = nodesBuffer
  }

  //move down the lines above the line cleared
  def moveLineDown(row: Int){
    //initialize the ObservableBuffer to store the nodes that are in the first row
    var nodesToBeDeleted: ObservableBuffer[Node] = ObservableBuffer()
    for(i <- row - 1 to 0 by -1){
      nodesBuffer.foreach(node => {
        if(GridPane.getRowIndex(node) == i)
          GridPane.setRowIndex(node, i+1)

        if(GridPane.getRowIndex(node) == 0)
          nodesToBeDeleted += node
      })
    }
    nodesBuffer --= nodesToBeDeleted
    resetBackgroundList()
    nodesBuffer.foreach(node => backgroundList(GridPane.getRowIndex(node))(GridPane.getColumnIndex(node)) = 1)
  }

  //check if the game is over
  def isGameOver(): Boolean = {
    if(currentBlock.Y < 0){
      currentBlock = null
      return true
    }
    false
  }

  //actions performed after game over
  def handleGameOver(){
    gameAreaAction.stop()
    gameOverMediaPlayer.play()
    var place = 1
    for(player <- Player.playerData){
      if(player.score.value>=scoreLabel.text().toInt){
        place = place + 1
      }
    }
    TetrisScalaFx.showGameOverDialog(place, score)
  }

  //actions performed after the return to main menu button is pressed
  def handleReturnToMainMenu(){
    gameAreaAction.stop()
    TetrisScalaFx.showMainMenu()
    TetrisScalaFx.showingGame.value = false
  }
}




