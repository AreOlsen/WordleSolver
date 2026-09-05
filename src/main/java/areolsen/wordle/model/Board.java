package areolsen.wordle.model;

import areolsen.grid.Position;
import areolsen.grid.BasicGrid;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.word.Word;

/**
 * This class represents the Wordle board and is a Grid of WordleCharacter.
 */
public class Board extends BasicGrid<Letter> {
    private int currentRow = 0;

    public Board(int rows, int cols) {
        super(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Position pos = new Position(j,i);
                Letter cg = new Letter(' ', LetterAnswerType.BLANK);
                this.set(pos, cg);
            }
        }
    }

    /**
     * Set the given WordleWord in the current selected row of the board. When the
     * word is set in this row, the board moves to the next row.
     *
     * @param wordGuess
     */
    public void setRow(Word wordGuess) {
        int col = 0;
        for (Letter cg : wordGuess) {
            Position pos = new Position(col++,currentRow);
            set(pos, cg);
        }
        currentRow++;
    }

    /**
     * Gets the current row of the board.
     * @return current row
     */
    public int getCurrentRow() {
        return currentRow;
    }
}
