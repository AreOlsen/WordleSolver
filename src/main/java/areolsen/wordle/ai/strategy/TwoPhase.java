package areolsen.wordle.ai.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import areolsen.wordle.model.Dictionary;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.model.word.Answer;
import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.word.Word;
import areolsen.wordle.model.word.WordList;

/**
 * Adaptive dual-phase Wordle strategy that optimizes based on remaining answer space size.
 *
 * <p>Phase 1: Uses probability-based exploration to maximize information gain
 * through unique letter discovery, efficiently reducing large search spaces.</p>
 *
 * <p>Phase 2: Switches to minimax optimization that minimizes worst-case
 * remaining answers, guaranteeing optimal performance in small search spaces.</p>
 */
public class TwoPhase implements Strategy {
    private static final int PROBABILITY_THRESHOLD = 80;
    private static final int SMALL_SEARCH_SPACE_THRESHOLD = 5;

    private Dictionary dictionary;
    private WordList currentWords;
    private HashSet<Integer> greenSlots;
    private HashSet<Character> greenLetters;
    private HashMap<Character, Double> letterProbabilities;

    /**
     * Constructs a new MyStrategy instance with the given dictionary.
     *
     * @param dictionary the dictionary containing valid words for guessing and answering
     */
    public TwoPhase(Dictionary dictionary) {
        this.dictionary = dictionary;
        reset();
    }

    @Override
    public String makeGuess(Word feedback) {
        String guess = "";

        //Extract previous feedback.
        if (feedback != null) {
            currentWords.eliminateWords(feedback);
            int position = 0;
            for (Letter character : feedback) {
                if (character.answerType == LetterAnswerType.CORRECT) {
                    greenSlots.add(position);
                    greenLetters.add(Character.toLowerCase(character.letter));
                }
                position++;
            }
        }

        //Phased guessing.
        letterProbabilities=TwoPhase.uniqueLetterOdds(currentWords.possibleAnswers(), greenSlots);
        if(currentWords.possibleAnswers().size() > PROBABILITY_THRESHOLD){
            guess = findBestByProbability(dictionary.getAnswerWordsList());
        } else if (currentWords.possibleAnswers().size() <= SMALL_SEARCH_SPACE_THRESHOLD) {
            guess = findBestByMinimax(currentWords.possibleAnswers());
        } else {
            guess = findBestByMinimax(dictionary.getGuessWordsList());
        }

        return guess;
    }


    @Override
    public void reset() {
        currentWords = new WordList(dictionary);
        letterProbabilities = new HashMap<>();
        greenLetters= new HashSet<>();
        greenSlots = new HashSet<>();
    }

    /**
     * Finds the best word using probability-based scoring from candidate words.
     *
     * @param words list of candidate words to evaluate
     * @return the word with the highest probability score
     */
    private String findBestByProbability(List<String> words){
        String bestWord = words.get(0);
        double bestScore = probabilityScore(bestWord);
        for(String word : words){
            double score = probabilityScore(word);
            if(score>bestScore){
                bestWord=word;
                bestScore=score;
            }
        }
        return bestWord;
    }

    /**
     * Scores words by unique letter frequency for exploration phase
     *
     * @param guess word to guess.
     * @return score (sum of individual probabilities) of the guess..
     */
    private double probabilityScore(String guess){
        double score = 0d;
        Set<Character> uniqueLettersScored = new HashSet<>();

        for(int i = 0; i < guess.length(); i++){
            if(greenSlots.contains(i)){
                continue; // Skip green positions
            }
            char letter = Character.toLowerCase(guess.charAt(i));
            if(greenLetters.contains(letter)){
                continue; // Skip known green letters
            }

            // Only score each unique letter once per word -> score = probability of green.
            if(uniqueLettersScored.add(letter)){
                score += letterProbabilities.getOrDefault(letter, 0.0);
            }
        }
        return score;
    }

    /**
     * Calculates the probability of each letter appearing in the remaining possible words,
     * excluding letters at solved (green) positions.
     *
     * @param words      list of words to analyze
     * @param greenSlots positions to exclude
     * @return map of character to probability (fraction of words containing the letter)
     *
     * @see #getUniqueLettersExcludingPositions(String, Set)
     */
    private static HashMap<Character, Double> uniqueLetterOdds(List<String> words, Set<Integer> greenSlots){
        HashMap<Character,Double> letterOdds = new HashMap<>();
        //Count amount of unique characters in all words, excluding green slot positions.
        for(String word : words){
            Set<Character> letters = getUniqueLettersExcludingPositions(word, greenSlots);
            for(Character letter : letters){
                letterOdds.put(letter, letterOdds.getOrDefault(letter, 0d) + 1);
            }
        }

        //Convert to probabilities.
        for(Map.Entry<Character, Double> letterCount : letterOdds.entrySet()){
            letterOdds.put(letterCount.getKey(), (double)(letterCount.getValue()/words.size()));
        }

        return letterOdds;
    }


    /**
     * Creates a set of unique letters from a string, excluding letters at green slot positions.
     * Converts all letters to lowercase for consistency.
     *
     * @param word the string to extract letters from
     * @param greenSlots set of positions to exclude
     * @return Set of unique characters not at green positions
     */
    private static Set<Character> getUniqueLettersExcludingPositions(String word, Set<Integer> greenSlots) {
        HashSet<Character> uniqueLetters = new HashSet<>();
        for (int i = 0; i < word.length(); i++) {
            if (!greenSlots.contains(i)) {  // Skip green slot positions
                uniqueLetters.add(Character.toLowerCase(word.charAt(i)));
            }
        }
        return uniqueLetters;
    }

    /**
     * Finds the guess that minimizes the worst-case number of remaining answers.
     *
     * @param words list of candidate words to evaluate
     * @return the word that minimizes worst-case remaining answers
     * @throws IllegalStateException if no possible answers remain
     */
    private String findBestByMinimax(List<String> words) {
        List<String> possibleAnswers = currentWords.possibleAnswers();

        if (possibleAnswers.isEmpty()) {
            throw new IllegalStateException("No answers remaining");
        }

        if (possibleAnswers.size() == 1) {
            return possibleAnswers.get(0);
        }

        String bestWord = words.get(0);
        int bestWorstCase = Integer.MAX_VALUE;

        for (String guess : words) {
            int worstCase = calculateWorstCaseRemaining(guess, possibleAnswers);
            if (worstCase < bestWorstCase) {
                bestWorstCase = worstCase;
                bestWord = guess;
            }
        }

        return bestWord;
    }

    /**
     * Calculates the worst-case number of remaining answers after making a guess.
     *
     * @param guess the word to evaluate as a potential guess
     * @param possibleAnswers list of currently possible answer words
     * @return the maximum number of answers that could remain after any feedback
     */
    private int calculateWorstCaseRemaining(String guess, List<String> possibleAnswers) {
        // Group answers by the feedback pattern they would produce
        Map<String, Integer> patternCounts = new HashMap<>();

        for (String answer : possibleAnswers) {
            String pattern = getPattern(Answer.matchWord(guess, answer));
            patternCounts.merge(pattern, 1, Integer::sum);
        }

        // Return the largest group size (worst case)
        int maxRemaining = 0;
        for (int count : patternCounts.values()) {
            maxRemaining = Math.max(maxRemaining, count);
        }

        return maxRemaining;
    }

    /**
     * Converts WordleWord feedback into a string pattern representation.
     *
     * @param feedback the WordleWord containing character feedback
     * @return string representation of the feedback pattern
     */
    private static String getPattern(Word feedback) {
        StringBuilder sb = new StringBuilder();
        for (Letter ch : feedback) {
            sb.append(ch.answerType.character);
        }
        return sb.toString();
    }

}
