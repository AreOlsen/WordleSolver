package no.uib.inf102.wordle.model.word;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import no.uib.inf102.util.MapUtil;
import no.uib.inf102.wordle.model.Dictionary;

/**
 * This class represents an answer to a Wordle puzzle.
 * 
 * The answer must be one of the words in the LEGAL_WORDLE_LIST.
 */
public class WordleAnswer {

    private final String WORD;

    private Dictionary dictionary;

    private static Random random = new Random();

    /**
     * Creates a WordleAnswer object with a random word from the answer word list
     */
    public WordleAnswer(Dictionary dictionary) {
        this(random, dictionary);
    }

    /**
     * Creates a WordleAnswer object with a random word from the answer word list
     * using a specified random object.
     * This gives us the opportunity to set a seed so that tests are repeatable.
     */
    public WordleAnswer(Random random, Dictionary dictionary) {
        this(getRandomWordleAnswer(random, dictionary), dictionary);
    }

    /**
     * Creates a WordleAnswer object with a given word.
     * 
     * @param answer
     * @param dictionary
     */
    public WordleAnswer(String answer, Dictionary dictionary) {
        this.WORD = answer.toLowerCase();
        this.dictionary = dictionary;
    }

    /**
     * Gets a random wordle answer
     * 
     * @param random
     * @return random string
     */
    private static String getRandomWordleAnswer(Random random, Dictionary dictionary) {
        List<String> possibleAnswerWords = dictionary.getAnswerWordsList();
        int randomIndex = random.nextInt(possibleAnswerWords.size());
        String newWord = possibleAnswerWords.get(randomIndex);
        return newWord;
    }

    /**
     * Guess the Wordle answer. Checks each character of the word guess and gives
     * feedback on which that is in correct position, wrong position and which is
     * not in the answer word.
     * This is done by updating the AnswerType of each WordleCharacter of the
     * WordleWord.
     * 
     * @param wordGuess
     * @return wordleWord with updated answertype for each character.
     */
    public WordleWord makeGuess(String wordGuess) {
        if (!dictionary.isLegalGuess(wordGuess))
            throw new IllegalArgumentException("The word '" + wordGuess + "' is not a legal guess");

        WordleWord guessFeedback = matchWord(wordGuess, WORD);
        return guessFeedback;
    }

    /**
     * Generates Wordle feedback by comparing a guess against the answer.
     * 
     * First marks exact matches (CORRECT), then misplaced letters (MISPLACED),
     * handling duplicate letters correctly by counting letter usage.
     * 
     * @param guess the player's guess word
     * @param answer the target answer word  
     * @return WordleWord with feedback (CORRECT/MISPLACED/WRONG) for each position
     * @throws IllegalArgumentException if words have different lengths
     */
    public static WordleWord matchWord(String guess, String answer) {
        int wordLength = answer.length();

        if (guess.length() != wordLength){
            throw new IllegalArgumentException("Guess and answer must have same number of letters but guess = " + guess
                    + " and answer = " + answer);
        }

        // SETUP: Initialize all positions as WRONG, will be updated as we find matches
        AnswerType[] feedback = new AnswerType[wordLength];
        for (int i = 0; i < wordLength; i++) {
        	feedback[i] = AnswerType.WRONG;
        }

        // Count how many times each letter appears in the answer
        HashMap<Character, Integer> answerLetterCount = new HashMap<>();
        for(char c : answer.toCharArray()){
            answerLetterCount.put(c, answerLetterCount.getOrDefault(c, 0)+1);
        }
        
        // Tracks usage of each letter in guess to avoid over-marking
        HashMap<Character, Integer> usedLetterCount = new HashMap<>();
        
        // FIRST PASS: Mark all exact position matches (CORRECT/Green)
        for(int i = 0; i < guess.length(); i++){
            char guessLetter = guess.charAt(i);

            if (guessLetter == answer.charAt(i)){
                feedback[i] = AnswerType.CORRECT;
                // Track that we've used one instance of this letter
                usedLetterCount.put(guessLetter, usedLetterCount.getOrDefault(guessLetter, 0) + 1);
            }
        }

        // SECOND PASS: Mark misplaced letters (MISPLACED/Yellow)
        for(int i = 0; i < guess.length(); i++){
            // Skip positions already marked as CORRECT
            if(feedback[i] == AnswerType.CORRECT){
                continue;
            }

            char guessLetter = guess.charAt(i);
            int timesUsed = usedLetterCount.getOrDefault(guessLetter, 0);
            int timesInAnswer = answerLetterCount.getOrDefault(guessLetter, 0);
            
            // Mark as MISPLACED if letter exists in answer and not all instances are used
            if (timesUsed < timesInAnswer) {
                feedback[i] = AnswerType.MISPLACED;
                usedLetterCount.put(guessLetter, timesUsed + 1);
            }
        }

        return new WordleWord(guess, feedback);
    }
}
