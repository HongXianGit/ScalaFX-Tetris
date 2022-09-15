package xian.hong.view

import xian.hong.model.Player
import xian.hong.TetrisScalaFx
import scalafx.scene.control.{Label, Alert, Button, ComboBox}
import scalafxml.core.macros.sfxml
import scalafx.stage.Stage
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.beans.property.{ObjectProperty,StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.{KeyEvent, KeyCode}

@sfxml
class GameOverDialogController (
  private val nameComboBox: ComboBox[String],
  private val placeLabel: Label,
  private val promptMessageLabel: Label,
  private val scoreLabel: Label,
  private val returnToHomePageButton: Button,
  private val playAgainButton: Button,
  private val switchModeButton: Button
){
  //window reference
  var dialogStage: Stage = null
  private var _score: ObjectProperty[Int] = ObjectProperty[Int](-1)
  private var _place: ObjectProperty[Int] = ObjectProperty[Int](-1)
  var selectedPlayerIndex: Int = -1
  var player: Player = null

  //the button turns into default buttons after it is focused
  returnToHomePageButton.defaultButton <== returnToHomePageButton.focused
  playAgainButton.defaultButton <== playAgainButton.focused
  switchModeButton.defaultButton <== switchModeButton.focused
  
  def score: ObjectProperty[Int] = _score
  def score_=(score: ObjectProperty[Int]) = {
    _score = score
    //set the score to the score label text
    scoreLabel.text = score.value.toString
  }

  def place: ObjectProperty[Int] = _place
  def place_=(place: ObjectProperty[Int]) = {
    _place = place
    //set the place to the place label text
    placeLabel.text = place.value.toString
  }

  //initialize the combo box
  nameComboBox.items = Player.playerData.map(_.playerName())

  //update selected player index if the player clicks on a certain name
  nameComboBox.selectionModel().selectedIndex.onChange((_, _, newValue) => selectedPlayerIndex = newValue.intValue())

  //actions performed when button is pressed
  def handleButtonAction(action: ActionEvent){
    //check if the input is valid
    if (isInputValid()) {
      //insert new record if the selected player index is -1
      if(selectedPlayerIndex == -1){ 
        val newPlayer: Player = new Player(StringProperty(nameComboBox.value()),ObjectProperty[Int](scoreLabel.text().toInt))                         
        Player.playerData += newPlayer
        newPlayer.save()
      }else{
        //update the record if the current score is better than the old score
        if(Player.playerData(selectedPlayerIndex).score.value<scoreLabel.text().toInt){
          Player.playerData(selectedPlayerIndex).score.value = scoreLabel.text().toInt
          Player.playerData(selectedPlayerIndex).save()
        }
      }
      dialogStage.close()
      //sort the data
      Player.playerData = Player.playerData.sortWith((x,y) => x.score.value > y.score.value)
      if(action.source.asInstanceOf[javafx.scene.control.Button].id().equals(returnToHomePageButton.id())){
        TetrisScalaFx.showMainMenu()
        TetrisScalaFx.showingGame.value = false
      }else{
        TetrisScalaFx.showGame()
      }
    }
  }

  //allow the player to enter in the combo box
  def handleAddNewName(action: ActionEvent) {
    if(!nameComboBox.editable()){
      nameComboBox.items = ObservableBuffer()
      selectedPlayerIndex = -1
      nameComboBox.editable = true
      switchModeButton.text = "Select existing player"
      nameComboBox.requestFocus()
    }else{
      nameComboBox.items = Player.playerData.map(_.playerName())
      nameComboBox.editable = false
      switchModeButton.text = "Add new player"
      nameComboBox.requestFocus()
    }
  }

  //check if the name is null of empty
  def nullChecking (x : String) = x == null || x.length == 0

  //check if the input is valid
  def isInputValid() : Boolean = {
    if (nameComboBox == null || !nullChecking(nameComboBox.value())) {
      true
    } else {
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Warning){
        initOwner(dialogStage)
        title = "Invalid name"
        headerText = "Please correct invalid name"
        contentText = "No valid name"
      }.showAndWait()
      false
    }
   }

  //display the popup when the player presses enter key
  def handleShow(key: KeyEvent){
    if(key.code == KeyCode.Enter){
      nameComboBox.show()
    }
  }
} 
