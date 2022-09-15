package xian.hong.view

import scalafx.scene.image.{Image, ImageView}
import xian.hong.TetrisScalaFx
import scalafxml.core.macros.sfxml

@sfxml
class InstructionController(
    private val backArrowImageView: ImageView
) {
  backArrowImageView.image = new Image(getClass.getResourceAsStream("/images/backArrow.png"))

  //actions performed after the back arrow is pressed
  def handleBackArrow(){
    TetrisScalaFx.showMainMenu()
    TetrisScalaFx.showingInstruction.value = false
  }
}