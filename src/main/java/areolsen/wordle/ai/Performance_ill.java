package areolsen.wordle.ai;

import java.util.List;
import java.util.Map;

import areolsen.wordle.ai.strategy.Strategy;
import areolsen.wordle.model.Dictionary;
import areolsen.wordle.resources.LoadFromFile5LetterEnglish;

public class Performance_ill {

	public static void main(String[] args) {
	    Dictionary dictionary = new Dictionary(LoadFromFile5LetterEnglish.GUESS_WORDS_LIST,LoadFromFile5LetterEnglish.ILL_WORDS_LIST);
		List<Strategy> strategies = AIPerformance.getStrategies(dictionary);
	    Map<Strategy, AIStatistics> stats = AIPerformance.runAll(strategies,dictionary);
	    AIPerformance.printAllResults(strategies, stats);
	}
}
