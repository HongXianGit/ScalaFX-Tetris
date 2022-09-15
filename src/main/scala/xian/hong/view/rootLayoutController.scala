package xian.hong.view

import scalafxml.core.macros.sfxml
import scalafx.application.Platform
import xian.hong.TetrisScalaFx
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.MenuItem
import scalafx.Includes._
import scalafx.beans.property.ObjectProperty

@sfxml
class RootLayoutController(
    private val backToMainMenuItem: MenuItem,
    private val newGameItem: MenuItem,
    private val leaderboardItem: MenuItem,
    private val instructionItem: MenuItem
){
    //bind these menu items to boolean variables to disable menu items dynamically
    backToMainMenuItem.disable <== TetrisScalaFx.showingMainMenu
    newGameItem.disable <== TetrisScalaFx.showingGame
    leaderboardItem.disable <== TetrisScalaFx.showingLeaderboard
    instructionItem.disable <== TetrisScalaFx.showingInstruction

    //action performed when close menu item is pressed
    def handleClose(){
        //Close the program
        Platform.exit()
    }

    //actions performed when back to main menu item is pressed
    def handleBackToMainMenu(){
        TetrisScalaFx.showMainMenu()
        stopGame()
        TetrisScalaFx.showingGame.value = false
        TetrisScalaFx.showingLeaderboard.value = false
        TetrisScalaFx.showingInstruction.value = false
    }

    //actions performed when new game menu item is pressed
    def handleNewGame(){
        TetrisScalaFx.showGame()
        stopTetrominoAnimation()
        TetrisScalaFx.showingMainMenu.value = false
        TetrisScalaFx.showingLeaderboard.value = false
        TetrisScalaFx.showingInstruction.value = false
    }

    //actions performed when high score item is pressed
    def handleLeaderboard(){
        TetrisScalaFx.showLeaderboard()
        stopGame()
        stopTetrominoAnimation()
        TetrisScalaFx.showingMainMenu.value = false
        TetrisScalaFx.showingGame.value = false
        TetrisScalaFx.showingInstruction.value = false
    }
    
    //actions performed when instruction menu item is pressed
    def handleInstruction(){
        TetrisScalaFx.showInstruction()
        stopGame()
        stopTetrominoAnimation()
        TetrisScalaFx.showingMainMenu.value = false
        TetrisScalaFx.showingLeaderboard.value = false
        TetrisScalaFx.showingGame.value = false
    }

    //actions performed when about menu item is pressed
    def handleAbout(){
        //show program information
        new Alert(Alert.AlertType.Information){
          initOwner(TetrisScalaFx.stage)
          title       = "About"
          headerText  = "Tetris V 1.0"
          contentText = "Copyrighted by Sunway University"
        }.showAndWait()
    }

    //stop the game animation when the player is navigating to other scene
    def stopGame(){
        if(TetrisScalaFx.showingGame.value){
            TetrisScalaFx.gameControllerOpt match {
                case Some(control) => {
                    control.gameAreaAction.stop()
                    TetrisScalaFx.gameControllerOpt = None
                }
                case None =>  throw new Exception("window initialize error")
            }
        }
    }

    //stop the tetrotromino animation when the player is navigating to other scene
    def stopTetrominoAnimation(){
        if(TetrisScalaFx.showingMainMenu.value){
            TetrisScalaFx.mainMenuControllerOpt match {
                case Some(control) => {
                    control.tetrominoAnimation.stop()
                    TetrisScalaFx.mainMenuControllerOpt = None
                }
                case None =>  throw new Exception("window initialize error")
            }
        }
    }
}