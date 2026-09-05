package areolsen.wordle.model.word;

/**
 * This enum represents the answer type a letter can have. The possible
 * answer types are BLANK, WRONG, MISPLACED and CORRECT.
 */
public enum LetterAnswerType {
    BLANK('b'),
    WRONG('w'),
    MISPLACED('m'),
    CORRECT('c');

    public final char character;

    private LetterAnswerType(char c) {
        this.character = c;
    }
}
