package areolsen.wordle.model.word;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Random;

import org.junit.jupiter.api.Test;

import areolsen.wordle.model.Dictionary;

public class AnswerTest {

    private Random random = new Random();

    private Dictionary dictionary = new Dictionary();

    private final String LEGAL_WORD = "arise";

    @Test
    public void allCorrectFeedback() {
        Answer answer = new Answer(LEGAL_WORD, dictionary);

        Word feedback = answer.makeGuess(LEGAL_WORD);
        for (Letter c : feedback) {
            assertEquals(LetterAnswerType.CORRECT, c.answerType);
        }
    }

    @Test
    public void allWrongPositionFeedback() {
        String answerString = "coast";
        String wrongPositionAnswerString = "tacos";
        Answer answer = new Answer(answerString, dictionary);

        Word feedback = answer.makeGuess(wrongPositionAnswerString);
        for (Letter c : feedback) {
            assertEquals(LetterAnswerType.MISPLACED, c.answerType);
        }
    }

    @Test
    public void allWrongFeedback() {
        String answerString = "coast";
        String wrongPositionAnswerString = "hurry";
        Answer answer = new Answer(answerString, dictionary);

        Word feedback = answer.makeGuess(wrongPositionAnswerString);
        for (Letter c : feedback) {
            assertEquals(LetterAnswerType.WRONG, c.answerType);
        }
    }

    @Test
    public void partialCorrectFeedback() {
        String answerString = "carry";
        String wrongPositionAnswerString = "hurry";
        Answer answer = new Answer(answerString, dictionary);

        Word feedback = answer.makeGuess(wrongPositionAnswerString);
        int i = 0;
        for (Letter c : feedback) {
            if (i < 2) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            else {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            i++;
        }
    }

    @Test
    public void correctSubstitutesWrongPosition() {
        Answer answer = new Answer("beast", dictionary);
        Word feedback = answer.makeGuess("adapt");

        int i = 0;
        for (Letter c : feedback) {
            if (i == 0) {
                assertEquals(LetterAnswerType.WRONG, c.answerType, "There is an 'a' in the answer, but it is located later at index 2. This 'a' should be WRONG.");
            }
            if (i == 1) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 2) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            if (i == 3) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 4) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            i++;
        }
    }

    @Test
    public void isPossibleWordRocks() {
        Answer answer = new Answer("rocks", dictionary);
        Word feedback = answer.makeGuess("sores");

        int i = 0;
        for (Letter c : feedback) {
            if (i == 0) {
                assertEquals(LetterAnswerType.WRONG, c.answerType, "The first 's' should be WRONG. There is only one 's' in 'rocks'. The second 's' should be CORRECT.");
            }
            if (i == 1) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            if (i == 2) {
                assertEquals(LetterAnswerType.MISPLACED, c.answerType);
            }
            if (i == 3) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 4) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            i++;
        }
        assertTrue(Word.isPossibleWord("rocks", feedback));
        assertFalse(Word.isPossibleWord("sores", feedback));
    }

    @Test
    public void isPossibleWordPoppyGuess() {
        Answer answer = new Answer("upper", dictionary);
        Word feedback = answer.makeGuess("poppy");

        int i = 0;
        for (Letter c : feedback) {
            if (i == 0) {
                assertEquals(LetterAnswerType.MISPLACED, c.answerType);
            }
            if (i == 1) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 2) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            if (i == 3) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 4) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            i++;
        }
        assertTrue(Word.isPossibleWord("upper", feedback));
        assertFalse(Word.isPossibleWord("poppy", feedback));
    }

    @Test
    public void isPossibleWordApoopGuess() {
        Answer answer = new Answer("poppy", dictionary);
        Word feedback = answer.makeGuess("apoop");

        int i = 0;
        for (Letter c : feedback) {
            if (i == 0) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 1) {
                assertEquals(LetterAnswerType.MISPLACED, c.answerType);
            }
            if (i == 2) {
                assertEquals(LetterAnswerType.MISPLACED, c.answerType);
            }
            if (i == 3) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 4) {
                assertEquals(LetterAnswerType.MISPLACED, c.answerType);
            }
            i++;
        }
        assertTrue(Word.isPossibleWord("poppy", feedback));
        assertFalse(Word.isPossibleWord("apoop", feedback));
    }

     @Test
    public void isPossibleWordMommy() {
        Answer answer = new Answer("mommy", dictionary);
        Word feedback = answer.makeGuess("money");

        int i = 0;
        for (Letter c : feedback) {
            if (i == 0) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            if (i == 1) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            if (i == 2) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 3) {
                assertEquals(LetterAnswerType.WRONG, c.answerType);
            }
            if (i == 4) {
                assertEquals(LetterAnswerType.CORRECT, c.answerType);
            }
            i++;
        }
        assertTrue(Word.isPossibleWord("mommy", feedback));
        assertTrue(Word.isPossibleWord("mossy", feedback));
    }

    @Test
    public void canCreateLegalWords() {
        for (String legalAnswerWord : dictionary.getAnswerWordsList()) {
            assertDoesNotThrow(() -> new Answer(legalAnswerWord, dictionary), "This word was not legal: " + legalAnswerWord);
        }
    }


    @Test
    public void cannotGuessNonsenseWords() {
        Answer answer = new Answer(LEGAL_WORD, dictionary);
        for (int i = 0; i < 1000; i++) {
            assertThrows(IllegalArgumentException.class, () -> answer.makeGuess(createNonsenseWord()));
        }
    }

    /**
     * Creates a word with a random jumble of characters
     *
     * @return nonsense word
     */
    private String createNonsenseWord() {
        int a = 97;
        int z = 122;
        while (true) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < dictionary.WORD_LENGTH; i++) {
                char c = (char) random.nextInt(a, z);
                sb.append(c);
            }
            String word = sb.toString();
            if (!dictionary.getGuessWordsList().contains(word))
                return word;
        }
    }

    @Test
    public void testLongWords() {
    	String longWord = makeLongWord(1000);
    	Word feedback = Answer.matchWord(longWord, longWord);
    	assertTrue(feedback.allMatch());
    	String longWordShifted = shiftString(longWord);
    	feedback = Answer.matchWord(longWord, longWordShifted);
    	for(Letter c:feedback) {
    		assertEquals(LetterAnswerType.MISPLACED,c.answerType);
    	}
    }

    @Test
    public void testMatchWordsIsFast() {
    	String longWord = makeLongWord(1000);
		String longWordShifted = shiftString(longWord);
    	for(int i=0; i<10; i++) {
    		longWord=longWordShifted;
    		longWordShifted = shiftString(longWord);

    		final String guess = longWord;
    		final String ans = longWordShifted;
    		Word feedback = assertTimeoutPreemptively(
    				Duration.ofMillis(200),
    				() -> Answer.matchWord(guess, ans),
    				"First makeGuess(null) took too long; your FrequencyStrategy is too slow."
    			);
    		for(Letter c:feedback) {
    			assertEquals(LetterAnswerType.MISPLACED,c.answerType);
    		}
    	}
    }

	private String shiftString(String longWord) {
		return longWord.charAt(longWord.length()-1)+longWord.substring(0, longWord.length()-1);
	}

	private String makeLongWord(int rounds) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<rounds; i++) {
			for(char c = 'a'; c<='z'; c++) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
