package secondmate

import org.gjt.sp.jedit.gui.DefaultInputHandler
import org.gjt.sp.jedit.Buffer
import org.gjt.sp.jedit.View
import org.gjt.sp.jedit.gui.DefaultInputHandler
import org.gjt.sp.jedit.gui.InputHandler
import org.gjt.sp.jedit.gui.KeyEventTranslator
import org.gjt.sp.jedit.jEdit
import org.gjt.sp.jedit.textarea.JEditTextArea
import org.gjt.sp.jedit.textarea.Selection
import java.awt.event.KeyEvent

/**
  * Created by weijiayi on 18/11/2016.
  */
class SecondMateInputHandlerScala(view: View) {

  def APOSTROPHE = "apostrophe"
  def QUOTE = "quote"
  def PARENTHESIS = "parenthesis"
  def BRACKET = "bracket"
  def BRACE = "brace"

  val pairs = Map[String, String](
    "(" -> ")", "[" -> "]", "{" -> "}", "\"" -> "\"", "`" -> "`"
  )


  def handleKey(keyStroke: KeyEventTranslator.Key, handler: IHandler): Boolean = {
    val buffer: Buffer = view.getBuffer
    val textArea: JEditTextArea = view.getTextArea
    val caret: Int = textArea.getCaretPosition
    val selections: Array[Selection] = textArea.getSelection


    def stringBeforeCaret(len: Int): Option[String] = {
      if(caret - len < 0) None
      else Some(buffer.getText(caret - len, len))
    }

    def stringAfterCaret(len: Int): Option[String] = {
      if(caret + len > buffer.getLength) None
      else Some(buffer.getText(caret, len))
    }

    def paired(left: Option[String], right: Option[String]): Boolean = {
      (left, right) match {
        case (Some(l), Some(r)) =>
          pairs.get(l).contains(r)
        case _ => false
      }
    }

    def pairedAroundCaret = paired(stringBeforeCaret(1), stringAfterCaret(1))

    if(selections.length>0) {
//      if(keyStroke.key == KeyEvent.VK_BACK_SPACE){
//        // eliminate redundant selections
//        val r = handler.handleKey(keyStroke)
//        textArea.setSelection(Array[Selection]())
//        buffer.insert(caret, s"selections: ${textArea.getSelectionCount}")
//        return r
//      }
      return handler.handleKey(keyStroke)
    }

    // figure out what to do
    if (keyStroke.key == KeyEvent.VK_BACK_SPACE) {
      if(pairedAroundCaret){
        buffer.remove(caret-1, 2)
        return true
      }
    } else {
      val input = keyStroke.input.toString

      if(pairedAroundCaret && stringAfterCaret(1).contains(input)){
        view.getTextArea.setCaretPosition(caret+1)
        return true
      }

      pairs.get(input) foreach { rightPair =>
        val shouldAutoClose = stringAfterCaret(1) match {
          case None => true
          case Some(s) => pairs.valuesIterator.contains(s) || s.matches("\\s")
        }
        if(shouldAutoClose){
          buffer.beginCompoundEdit()
          buffer.insert(caret, keyStroke.input.toString + rightPair)
          view.getTextArea.setCaretPosition(caret+1)
          buffer.endCompoundEdit()
          return true
        }
      }
    }

    handler.handleKey(keyStroke)
  }

  private def getPairEnabled (mode: String, `type`: String): Boolean = {
    jEdit.getBooleanProperty("mode." + mode + ".pair." + `type`, true)
  }
}
