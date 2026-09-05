package areolsen.wordle.ai.strategy;

import java.util.Random;

import areolsen.wordle.model.Dictionary;
import areolsen.wordle.model.word.Word;
import areolsen.wordle.model.word.WordList;

/**
 * This strategy eliminates guesses that are impossible with the feedback given
 * throughout the game.
 * For example:
 * If the answer is "break" and you answer "chest", you will get
 * feedback showing that the middle "e" is in the right position. Therefore you
 * eliminate all words that do not have an e in the middle position.
 */
public class EliminateStrategy implements Strategy {

    private Dictionary dictionary;
    private WordList currentWords;
    private Random random = new Random();

    public EliminateStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(Word feedback) {
        if (feedback != null) {
            currentWords.eliminateWords(feedback);
        }

        // Choose a random word and return it from the possible guesses after eliminating
        int randIndex = random.nextInt(currentWords.size());
        return currentWords.possibleAnswers().get(randIndex);
    }

    @Override
    public void reset() {
        currentWords = new WordList(dictionary);
    }
}
