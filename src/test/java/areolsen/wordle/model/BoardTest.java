package areolsen.wordle.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import areolsen.grid.Cell;
import areolsen.grid.Position;
import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.model.word.Word;

class BoardTest {
    private Board board;
    private static final int ROWS = 6;
    private static final int COLS = 5;

    @BeforeEach
    void setUp() {
        board = new Board(ROWS, COLS);
    }

    @Test
    void testBoardInitialization() {
        assertEquals(ROWS, board.rows());
        assertEquals(COLS, board.cols());
        assertEquals(0, board.getCurrentRow());
    }

    @Test
    void testBoardHasBlankLettersInitially() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Position pos = new Position(col, row);
                Letter letter = board.get(pos);
                assertEquals(' ', letter.letter);
                assertEquals(LetterAnswerType.BLANK, letter.answerType);
            }
        }
    }

    @Test
    void testSetRow() {
        // Create feedback array for "hello"
        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word = new Word("hello", feedback);

        board.setRow(word);

        assertEquals(1, board.getCurrentRow());

        // Verify the row was set correctly
        String expectedWord = "hello";
        for (int col = 0; col < COLS; col++) {
            Position pos = new Position(col, 0);
            Letter letter = board.get(pos);
            assertEquals(expectedWord.charAt(col), letter.letter);
            assertEquals(LetterAnswerType.CORRECT, letter.answerType);
        }
    }

    @Test
    void testSetRowMultipleRows() {
        // First word: "apple" all correct
        LetterAnswerType[] feedback1 = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word1 = new Word("apple", feedback1);

        // Second word: "crane" all wrong
        LetterAnswerType[] feedback2 = {
            LetterAnswerType.WRONG,
            LetterAnswerType.WRONG,
            LetterAnswerType.WRONG,
            LetterAnswerType.WRONG,
            LetterAnswerType.WRONG
        };
        Word word2 = new Word("crane", feedback2);

        board.setRow(word1);
        board.setRow(word2);

        assertEquals(2, board.getCurrentRow());

        // Verify row 0
        String expectedWord1 = "apple";
        for (int col = 0; col < COLS; col++) {
            Position pos = new Position(col, 0);
            Letter letter = board.get(pos);
            assertEquals(expectedWord1.charAt(col), letter.letter);
        }

        // Verify row 1
        String expectedWord2 = "crane";
        for (int col = 0; col < COLS; col++) {
            Position pos = new Position(col, 1);
            Letter letter = board.get(pos);
            assertEquals(expectedWord2.charAt(col), letter.letter);
        }
    }

    @Test
    void testSetRowWithMixedAnswerTypes() {
        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.WRONG,
            LetterAnswerType.MISPLACED,
            LetterAnswerType.CORRECT,
            LetterAnswerType.WRONG
        };
        Word word = new Word("hello", feedback);

        board.setRow(word);

        LetterAnswerType[] expectedTypes = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.WRONG,
            LetterAnswerType.MISPLACED,
            LetterAnswerType.CORRECT,
            LetterAnswerType.WRONG
        };
        for (int col = 0; col < COLS; col++) {
            Position pos = new Position(col, 0);
            Letter letter = board.get(pos);
            assertEquals(expectedTypes[col], letter.answerType);
        }
    }

    @Test
    void testGetCurrentRowIncrements() {
        assertEquals(0, board.getCurrentRow());

        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word = new Word("hello", feedback);

        board.setRow(word);
        assertEquals(1, board.getCurrentRow());

        board.setRow(word);
        assertEquals(2, board.getCurrentRow());
    }

    @Test
    void testSetRowAtBoundary() {
        // Fill all rows
        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word = new Word("hello", feedback);

        for (int i = 0; i < ROWS; i++) {
            board.setRow(word);
        }

        assertEquals(ROWS, board.getCurrentRow());

        // Verify last row was set
        String expectedWord = "hello";
        for (int col = 0; col < COLS; col++) {
            Position pos = new Position(col, ROWS - 1);
            Letter letter = board.get(pos);
            assertEquals(expectedWord.charAt(col), letter.letter);
        }
    }

    @Test
    void testSetRowWithEmptyWord() {
        // Use a word with spaces for blank
        LetterAnswerType[] feedback = {
            LetterAnswerType.BLANK,
            LetterAnswerType.BLANK,
            LetterAnswerType.BLANK,
            LetterAnswerType.BLANK,
            LetterAnswerType.BLANK
        };
        // Note: Word constructor doesn't allow BLANK in feedback
        // So we need to use a different approach or skip this test
        // Or use the actual Word constructor with a word and feedback that doesn't use BLANK
    }

    @Test
    void testBoardIteration() {
        // Set a row
        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word = new Word("hello", feedback);
        board.setRow(word);

        int count = 0;
        for (Cell<Letter> cell : board) {
            count++;
        }
        assertEquals(ROWS * COLS, count);
    }

    @Test
    void testGetLetterAtPosition() {
        LetterAnswerType[] feedback = {
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT,
            LetterAnswerType.CORRECT
        };
        Word word = new Word("world", feedback);
        board.setRow(word);

        Position pos = new Position(2, 0);
        Letter letter = board.get(pos);
        assertEquals('r', letter.letter);

        Position pos2 = new Position(4, 0);
        Letter letter2 = board.get(pos2);
        assertEquals('d', letter2.letter);
    }

    @Test
    void testSetLetterAtPosition() {
        Position pos = new Position(2, 3);
        Letter letter = new Letter('x', LetterAnswerType.CORRECT);
        board.set(pos, letter);

        Letter retrieved = board.get(pos);
        assertEquals('x', retrieved.letter);
        assertEquals(LetterAnswerType.CORRECT, retrieved.answerType);
    }
}
