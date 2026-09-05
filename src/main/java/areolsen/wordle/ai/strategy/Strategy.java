package areolsen.wordle.ai.strategy;

import areolsen.wordle.model.word.Word;

public interface Strategy {

    /**
     * Make a Wordle guess based on the given <code>feedback</code>.
     * @param feedback
     * @return the guess
     */
    String makeGuess(Word feedback);

    /**
     * This method is called when there is a new word to guess.
     * It should reset any internal variables to make guesses for a new word.
     */
    void reset();
}
