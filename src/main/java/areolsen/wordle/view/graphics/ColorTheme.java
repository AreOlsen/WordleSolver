package areolsen.wordle.view.graphics;

import java.awt.Color;

import areolsen.wordle.model.word.LetterAnswerType;

public interface ColorTheme {
  Color getCellColor(LetterAnswerType ansType);
  Color getFrameColor();
  Color getBackgroundColor();
}
