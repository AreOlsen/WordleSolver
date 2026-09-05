package areolsen.wordle.view.graphics;

import java.awt.Color;

import areolsen.wordle.model.word.LetterAnswerType;

public class DefaultColorTheme implements ColorTheme {

  @Override
  public Color getCellColor(LetterAnswerType ansType) {
    Color color = switch(ansType) {
      case BLANK -> new Color(255, 249, 251);
      case WRONG -> new Color(251, 99, 118);
      case MISPLACED -> new Color(255, 217, 125);
      case CORRECT -> new Color(170, 246, 131);
      default -> throw new IllegalArgumentException(
        "No available color for '" + ansType + "'");
    };
    return color;
  }

  @Override
  public Color getFrameColor() {
    return new Color(0, 0, 0, 0);
  }

  @Override
  public Color getBackgroundColor() {
    return null;
  }

}
