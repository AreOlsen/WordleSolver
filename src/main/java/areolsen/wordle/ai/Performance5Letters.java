package areolsen.wordle.ai;

import java.util.List;
import java.util.Map;

import areolsen.wordle.ai.strategy.Strategy;
import areolsen.wordle.model.Dictionary;

public class Performance5Letters {

	public static void main(String[] args) {
        Dictionary dictionary = new Dictionary();
    	List<Strategy> strategies = AIPerformance.getStrategies(dictionary);
        Map<Strategy, AIStatistics> stats = AIPerformance.runAll(strategies,dictionary);
        AIPerformance.printAllResults(strategies, stats);
	}

}
