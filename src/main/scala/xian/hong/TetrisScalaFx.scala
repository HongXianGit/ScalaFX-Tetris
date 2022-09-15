package xian.hong

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{NoDependencyResolver, FXMLView, FXMLLoader}
import javafx.{scene => jfxs}
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.{ObjectProperty,BooleanProperty}
import xian.hong.model.Player
import xian.hong.view.{GameOverDialogController,GameController,MainMenuController}
import scalafx.stage.{Stage, Modality}
import scalafx.scene.image.Image
import xian.hong.util.Database
import scalafx.scene.media.{Media,MediaPlayer}
import scalafx.beans.value.ObservableValue

object TetrisScalaFx extends JFXApp {
  Database.setupDB() //initialize

  //initialize the player data observable buffer and sort it
  Player.playerData ++= Player.allPlayers.sortWith((x,y) => x.score.value > y.score.value)

  //option to hold the game controller object to see if the player is playing game
  var gameControllerOpt: Option[GameController#Controller] = None

  //option to hold the main menu controller object to see if the player is at main menu
  var mainMenuControllerOpt: Option[MainMenuController#Controller] = None

  //variables to know which scene is showing
  val showingMainMenu = ObjectProperty[java.lang.Boolean](false)
  val showingGame = ObjectProperty[java.lang.Boolean](false)
  val showingLeaderboard = ObjectProperty[java.lang.Boolean](false)
  val showingInstruction = ObjectProperty[java.lang.Boolean](false)

  // transform path of RootLayout.fxml to URI for resource location.
  val rootResource = getClass.getResourceAsStream("view/RootLayout.fxml")

  // initialize the loader object.
  val loader = new FXMLLoader(null, NoDependencyResolver)

  // Load root layout from fxml file.
  loader.load(rootResource);

  // retrieve the root component BorderPane from the FXML 
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  // initialize stage
  stage = new PrimaryStage {
    title = "Tetris"
    scene = new Scene {
      root = roots
      stylesheets += getClass.getResource("view/Tetris.css").toString()
    }
    icons += new Image(getClass.getResourceAsStream("/images/tetris.png"))
  }

  //set the min size and max size for the stage
  stage.minWidth = 300
  stage.minHeight = 700

  //initialize media and media player to play background music
  val backgroundMedia = new Media(getClass.getResource("/sound/tetrisBgm.mp3").toString())
  val backgroundMediaPlayer = new MediaPlayer(backgroundMedia)
  backgroundMediaPlayer.cycleCount = MediaPlayer.Indefinite
  backgroundMediaPlayer.volume = 0.1
  backgroundMediaPlayer.play()
  

  //actions for displaying main page scene
  def showMainMenu() = {
    val resource = getClass.getResourceAsStream("view/MainMenu.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    mainMenuControllerOpt = Option(loader.getController[MainMenuController#Controller])
    showingMainMenu.value = true
  } 

  //actions for displaying game scene
  def showGame() = {
    val resource = getClass.getResourceAsStream("view/Game.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots = loader.getRoot[jfxs.control.SplitPane]
    this.roots.setCenter(roots)
    showingGame.value = true
    gameControllerOpt = Option(loader.getController[GameController#Controller])
  } 

  //actions for displaying game over scene
  def showGameOverDialog(_place: Int, _score: ObjectProperty[Int]){
    val resource = getClass.getResourceAsStream("view/GameOverDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots  = loader.getRoot[jfxs.layout.VBox]
    val control = loader.getController[GameOverDialogController#Controller]

    val dialog = new Stage() {
      title = "Game Over!!!"
      resizable = false
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      scene = new Scene {
        root = roots
        stylesheets += getClass.getResource("view/Tetris.css").toString()
      }
      icons += new Image(getClass.getResourceAsStream("/images/bomb.png"))
    }
    dialog.onCloseRequest = (x) => {
      showingGame.value = false
      showMainMenu()
    }
    control.dialogStage = dialog
    //transfer the score and place to the game over dialog controller object
    control.score = _score
    control.place = ObjectProperty[Int](_place)
    dialog.show()
  }

  //actions for displaying high score scene
  def showLeaderboard(){
    val resource = getClass.getResourceAsStream("view/Leaderboard.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots = loader.getRoot[jfxs.layout.VBox]
    this.roots.setCenter(roots)
    showingLeaderboard.value = true
  }
  
  //actions for displaying instruction scene
  def showInstruction(){
    val resource = getClass.getResourceAsStream("view/Instruction.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource);
    val roots = loader.getRoot[jfxs.layout.GridPane]
    this.roots.setCenter(roots)
    showingInstruction.value = true
  }

  // call to display main menu when app start
  showMainMenu()
}
