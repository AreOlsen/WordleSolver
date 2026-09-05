package areolsen.wordle.controller.AI;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import areolsen.wordle.ai.strategy.EliminateStrategy;
import areolsen.wordle.model.Dictionary;
import areolsen.wordle.model.word.Answer;
import areolsen.wordle.model.word.Word;

public class EliminateStrategyTest {

	private Dictionary dictionary = new Dictionary();

	@Test
	void testComputeMakeGuess() {
		EliminateStrategy ai = new EliminateStrategy(dictionary);
		ArrayList<Word> feedback = new ArrayList<>();
		ArrayList<String> guesses = new ArrayList<>();
		Answer ans = new Answer("cloud", dictionary);
		Word lastFeedback = null;
		int rounds = 0;
		while(true) {
			String guess = ai.makeGuess(lastFeedback);
			assertFalse(guesses.contains(guess),"should not guess same word twice");
			//EliminateStrategy must always guess words compatible with previous feedback.
			for(Word f : feedback) {
				assertTrue(Word.isPossibleWord(guess, f),"Guess: "+guess +" not compatible with "+f);
			}
			lastFeedback = ans.makeGuess(guess);
			if(lastFeedback.allMatch())
				break;
			feedback.add(lastFeedback);
			rounds++;
			//When EliminateStrategy guesses a letter that becomes grey or yellow
			//meaning one should never guess same letter again on that position unless it is green
			assertTrue(rounds<=26);
		}
	}

}
