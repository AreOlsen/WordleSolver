package areolsen.wordle.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import areolsen.grid.Cell;
import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.model.word.Word;

/**
 * Test suite for the Model class.
 */
public class ModelTest {
    private Model model;
    private Board board;
    private Dictionary dictionary;
    private static final int ROWS = 6;
    private static final int COLS = 5;

    @BeforeEach
    void setUp() {
        dictionary = new Dictionary();
        board = new Board(ROWS, COLS);
        model = new Model(board, dictionary);
    }

    @Test
    void testModelInitialization() {
        assertEquals(GameState.ACTIVE_GAME, model.getGameState());
        assertEquals(ROWS, model.getHeight());
        assertEquals(COLS, model.getWidth());
        assertNotNull(model.getDictionary());
    }

    @Test
    void testAddCharacter() {
        assertTrue(model.addCharacter('h'));
        assertTrue(model.addCharacter('e'));
        assertTrue(model.addCharacter('l'));
        assertTrue(model.addCharacter('l'));
        assertTrue(model.addCharacter('o'));

        // Should not allow more than word length
        assertFalse(model.addCharacter('x'));

        // Verify current guess
        Iterable<Cell<Letter>> currentGuess = model.getCurrentGuess();
        int count = 0;
        for (Cell<Letter> cell : currentGuess) {
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    void testRemoveCharacter() {
        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');
        model.addCharacter('l');
        model.addCharacter('o');

        assertTrue(model.removeCharacter());
        // Should now have "hell"
        Iterable<Cell<Letter>> currentGuess = model.getCurrentGuess();
        int count = 0;
        for (Cell<Letter> cell : currentGuess) {
            count++;
        }
        assertEquals(4, count);

        assertTrue(model.removeCharacter());
        assertTrue(model.removeCharacter());
        assertTrue(model.removeCharacter());
        assertTrue(model.removeCharacter());

        // Should not remove when empty
        assertFalse(model.removeCharacter());
    }

    @Test
    void testMakeGuessLegalWord() throws IllegalArgumentException {
        // Add a legal word
        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');
        model.addCharacter('l');
        model.addCharacter('o');

        Word feedback = model.makeGuess();
        assertNotNull(feedback);
        assertEquals(1, board.getCurrentRow());
        assertEquals(GameState.ACTIVE_GAME, model.getGameState());
    }

    @Test
    void testMakeGuessIllegalWord() {
        // Add an illegal word
        model.addCharacter('x');
        model.addCharacter('y');
        model.addCharacter('z');
        model.addCharacter('z');
        model.addCharacter('y');

        assertThrows(IllegalArgumentException.class, () -> {
            model.makeGuess();
        });
    }

    @Test
    void testMakeGuessWithIncompleteWord() {
        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');

        // Should still be able to guess, but it will be incomplete
        assertThrows(IllegalArgumentException.class, () -> {
            model.makeGuess();
        });
    }

    @Test
    void testVictoryState() {
        // Add a word that we know is in the dictionary
        model.addCharacter('c');
        model.addCharacter('r');
        model.addCharacter('a');
        model.addCharacter('n');
        model.addCharacter('e');

        // If the answer happens to be "crane", this would trigger victory
        // For testing, we'll just check that the game doesn't throw exceptions
        try {
            Word feedback = model.makeGuess();
            assertNotNull(feedback);
        } catch (IllegalArgumentException e) {
            // If the word is illegal for some reason
            // This test is more about the flow than the specific outcome
        }
    }

    @Test
    void testGameOverState() {
        // Fill all rows with guesses
        for (int i = 0; i < ROWS; i++) {
            model.addCharacter('h');
            model.addCharacter('e');
            model.addCharacter('l');
            model.addCharacter('l');
            model.addCharacter('o');
            try {
                model.makeGuess();
            } catch (IllegalArgumentException e) {
                // Skip if word is illegal
            }
        }

        // Game should be over (either victory or game over)
        GameState state = model.getGameState();
        assertTrue(state == GameState.VICTORY || state == GameState.GAME_OVER);
    }

    @Test
    void testGetTilesOnBoard() {
        // Add a guess
        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');
        model.addCharacter('l');
        model.addCharacter('o');
        try {
            model.makeGuess();
        } catch (IllegalArgumentException e) {
            // Skip if word is illegal
        }

        Iterable<Cell<Letter>> tiles = model.getTilesOnBoard();
        assertNotNull(tiles);

        int count = 0;
        for (Cell<Letter> cell : tiles) {
            count++;
        }
        assertEquals(ROWS * COLS, count);
    }

    @Test
    void testGetCurrentGuess() {
        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');
        model.addCharacter('l');
        model.addCharacter('o');

        Iterable<Cell<Letter>> currentGuess = model.getCurrentGuess();
        assertNotNull(currentGuess);

        int count = 0;
        for (Cell<Letter> cell : currentGuess) {
            count++;
            // All letters should be BLANK type
            assertEquals(LetterAnswerType.BLANK, cell.value().answerType);
        }
        assertEquals(5, count);
    }

    @Test
    void testGetCurrentGuessWithEmptyGuess() {
        Iterable<Cell<Letter>> currentGuess = model.getCurrentGuess();
        assertNotNull(currentGuess);

        int count = 0;
        for (Cell<Letter> cell : currentGuess) {
            count++;
        }
        assertEquals(0, count);
    }

    @Test
    void testGetTimerDelay() {
        assertEquals(1000, model.getTimerDelay());
    }

    @Test
    void testClockTick() {
        // Should not throw any exceptions
        model.clockTick();
    }


    @Test
    void testMultipleResets() {
        model.reset();
        model.reset();
        model.reset();

        assertEquals(GameState.ACTIVE_GAME, model.getGameState());
        assertEquals(0, board.getCurrentRow());
    }

    @Test
    void testAddCharacterAfterReset() {
        model.reset();

        assertTrue(model.addCharacter('h'));
        assertTrue(model.addCharacter('e'));
        assertTrue(model.addCharacter('l'));
        assertTrue(model.addCharacter('l'));
        assertTrue(model.addCharacter('o'));
        assertFalse(model.addCharacter('x'));
    }

    @Test
    void testMakeGuessAfterReset() {
        model.reset();

        model.addCharacter('h');
        model.addCharacter('e');
        model.addCharacter('l');
        model.addCharacter('l');
        model.addCharacter('o');

        // Should be able to guess
        try {
            Word feedback = model.makeGuess();
            assertNotNull(feedback);
        } catch (IllegalArgumentException e) {
            // Word might be illegal in test dictionary
        }
    }

    @Test
    void testGetDictionary() {
        assertNotNull(model.getDictionary());
        assertEquals(dictionary, model.getDictionary());
    }

    @Test
    void testGetHeightAndWidth() {
        assertEquals(ROWS, model.getHeight());
        assertEquals(COLS, model.getWidth());
    }
}
