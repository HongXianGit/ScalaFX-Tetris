package xian.hong.view

import scalafx.scene.control.Button
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml
import scalafx.scene.image.{Image, ImageView}
import scalafx.application.Platform
import xian.hong.TetrisScalaFx
import scalafx.animation.{Timeline,KeyFrame}
import scalafx.util.Duration
import scala.util.Random

@sfxml
class MainMenuController(
    private val animationArea: AnchorPane,
    private val squareTetrominoImageView: ImageView,
    private val tetrisHeaderImageView: ImageView,
    private val playNowButton: Button,
    private val leaderboardButton: Button,
    private val instructionButton: Button,
    private val exitButton: Button
) {
    //initial speed of the squareTetrominoImageView
    var speedX = 10
    var speedY = 10

    //initialize the image of the animated item
    squareTetrominoImageView.image = new Image(getClass.getResourceAsStream("/images/squareTetromino.png"))
    
    //randomize the initial coordinate of the animated item
    squareTetrominoImageView.relocate(Random.nextInt((animationArea.minWidth()-squareTetrominoImageView.fitWidth()).toInt), Random.nextInt((animationArea.minHeight()-squareTetrominoImageView.fitHeight()).toInt))

    //initialize the photo to be shown as a header
    tetrisHeaderImageView.image = new Image(getClass.getResourceAsStream("/images/tetrisHeader.png"))

    //initialize the animation
    val tetrominoAnimation = new Timeline{
        cycleCount = Timeline.Indefinite
        //move the square tetromino every 100ms
        keyFrames += KeyFrame(Duration(100), onFinished = (e) => {
            if(squareTetrominoImageView.layoutY() + squareTetrominoImageView.fitHeight() >= animationArea.height() || squareTetrominoImageView.layoutY() < 0)
                speedY *= -1

            if(squareTetrominoImageView.layoutX() + squareTetrominoImageView.fitWidth() >= animationArea.width() || squareTetrominoImageView.layoutX() < 0)
                speedX *= -1

            squareTetrominoImageView.relocate(squareTetrominoImageView.layoutX() + speedX, squareTetrominoImageView.layoutY() + speedY)
        })
    }

    //play the animation
    tetrominoAnimation.play()

    //the button turns into default buttons after it is focused
    playNowButton.defaultButton <== playNowButton.focused
    leaderboardButton.defaultButton <== leaderboardButton.focused
    instructionButton.defaultButton <== instructionButton.focused
    exitButton.defaultButton <== exitButton.focused

    //method to handle play now button
    def handlePlayNow(){
        tetrominoAnimation.stop()
        TetrisScalaFx.showGame()
        TetrisScalaFx.showingMainMenu.value = false
    }

    //method to handle high score button
    def handleLeaderboard(){
        tetrominoAnimation.stop()
        TetrisScalaFx.showLeaderboard()
        TetrisScalaFx.showingMainMenu.value = false
    }

    //method to handle instruction button
    def handleInstruction(){
        tetrominoAnimation.stop()
        TetrisScalaFx.showInstruction()
        TetrisScalaFx.showingMainMenu.value = false
    }

    //method to handle exit button
    def handleExit(){
        Platform.exit()
    }
}
