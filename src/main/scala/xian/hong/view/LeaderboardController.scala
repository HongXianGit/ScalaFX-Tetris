package xian.hong.view

import scalafx.Includes._
import xian.hong.model.Player
import xian.hong.TetrisScalaFx
import scalafx.scene.control.{TableView, TableColumn, Label, Alert}
import scalafxml.core.macros.sfxml
import scalafx.beans.property.{StringProperty,ObjectProperty}
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image,ImageView}

@sfxml
class LeaderboardController(
    private val playerTable: TableView[Player],
    private val placeColumn: TableColumn[Player,Int],
    private val playerNameColumn: TableColumn[Player,String],
    private val scoreColumn: TableColumn[Player,Int],
    private val backArrowImageView: ImageView
) {
  //actions performed after the back arrow is pressed
  backArrowImageView.image = new Image(getClass.getResourceAsStream("/images/backArrow.png"))

  //make the table unselectable
  playerTable.selectionModel = null

  //initialize Table View display contents model
  //only display top 5 players
  playerTable.items = Player.playerData.slice(0, 5)

  //initialize columns's cell values
  placeColumn.cellValueFactory = {cellDataFeature => ObjectProperty[Int](playerTable.items().indexOf(cellDataFeature.value)+1)}
  playerNameColumn.cellValueFactory  = {_.value.playerName} 
  scoreColumn.cellValueFactory  = {_.value.score}

  //actions performed after the back arrow is pressed
  def handleBackArrow(){
    TetrisScalaFx.showMainMenu()
    TetrisScalaFx.showingLeaderboard.value = false
  }
}
