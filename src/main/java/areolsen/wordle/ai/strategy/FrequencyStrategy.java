package areolsen.wordle.ai.strategy;

import java.util.HashMap;
import java.util.Map;

import areolsen.wordle.model.Dictionary;
import areolsen.wordle.model.word.Word;
import areolsen.wordle.model.word.WordList;

/**
 * Strategy that maximizes expected green (correct position) matches.
 * Calculates position-based letter probabilities and selects the word
 * with the highest expected number of green letters.
 */
public class FrequencyStrategy implements Strategy {

    protected Dictionary dictionary;
    protected WordList currentWords;
    private Map<Integer, Map<Character, Double>> positionLetterProbabilities;

    public FrequencyStrategy(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(Word feedback) {
        if (feedback != null) {
            currentWords.eliminateWords(feedback);
        }

        calculatePositionLetterProbabilities(); // Recalculate probabilities.
        return getBestWordByExpectedGreenMatches(); //Find best guess.
    }


    @Override
    public void reset() {
        currentWords = new WordList(dictionary);
        positionLetterProbabilities = null;
    }

    /**
     * Calculates letter probabilities for each position based on remaining possible answers.
     */
    private void calculatePositionLetterProbabilities() {
        positionLetterProbabilities = new HashMap<>();

        //Nothing to create a map over.
        if (currentWords.possibleAnswers().isEmpty()) {
            return;
        }

        int wordLength = currentWords.wordLength();
        int totalWords = currentWords.possibleAnswers().size();

        // Calculate probabilities for each position
        for (int position = 0; position < wordLength; position++) {
            Map<Character, Integer> letterCounts = new HashMap<>();

            // Count letter occurrences at this position
            for (String word : currentWords.possibleAnswers()) {
                char letter = Character.toLowerCase(word.charAt(position));
                letterCounts.put(letter, letterCounts.getOrDefault(letter, 0) + 1);
            }

            // Convert counts to probabilities
            Map<Character, Double> letterProbs = new HashMap<>();
            for (Map.Entry<Character, Integer> entry : letterCounts.entrySet()) {
                double probability = (double) entry.getValue() / totalWords;
                letterProbs.put(entry.getKey(), probability);
            }

            //Store probabilities for position.
            positionLetterProbabilities.put(position, letterProbs);
        }
    }

    /**
     * Calculates expected number of green matches for a word.
     *
     * @param word word to evaluate
     * @return expected green matches (sum of position probabilities)
     */
    private double calculateExpectedGreenMatches(String word) {
        if (positionLetterProbabilities == null || positionLetterProbabilities.isEmpty()) {
            return 0.0;
        }

        double expectedGreenMatches = 0.0d;

        // Sum probability for each letter at its position
        for (int position = 0; position < word.length(); position++) {
            char letter = Character.toLowerCase(word.charAt(position));
            Map<Character, Double> positionProbs = positionLetterProbabilities.get(position);

            if (positionProbs != null) {
                expectedGreenMatches += positionProbs.getOrDefault(letter, 0.0d);
            }
        }

        //Convert probabilities to expected green matches.
        expectedGreenMatches*=currentWords.wordLength();

        return expectedGreenMatches;
    }

    /**
     * Finds the word with highest expected green matches.
     *
     * @return String best word to guess
     * @throws IllegalStateException if there are no possible answers remaining
     */
    private String getBestWordByExpectedGreenMatches() {
        if (currentWords.possibleAnswers().isEmpty()) {
            throw new IllegalStateException("No possible answers remaining");
        }

        String bestWord = currentWords.possibleAnswers().get(0);
        double bestScore = calculateExpectedGreenMatches(bestWord);

        // Find word with highest expected green matches.
        for (String word : currentWords.possibleAnswers()) {
            double score = calculateExpectedGreenMatches(word);
            if (score > bestScore) {
                bestScore = score;
                bestWord = word;
            }
        }

        return bestWord;
    }


}
