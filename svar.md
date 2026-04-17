# Runtime Analysis

For each method of the tasks give a runtime analysis in Big-O notation and a description of why it has this runtime.

**If you have implemented new methods not listed you must add these as well, e.g. any helper methods. You need to show how you analyzed any methods used by the methods listed below.**

The runtime should be expressed using these three parameters:

- `n` - number of words in the list allWords
- `m` - number of words in the list possibleWords
- `k` - number of letters in the wordleWords

## Task 1 - matchWord

- `WordleAnswer::matchWord`: O(k)
  Each of these operations occurs independently and sequentially, not nested within one another.
  - Initialize feedback array: O(k) - iterates through all k positions
  - Count letters in answer: O(k) - iterates through each character in answer
  - First pass: O(k) - checks each position for exact matches (green)
  - Second pass: O(k) - checks each position for misplaced letters (yellow)
  - HashMap operations: O(1) average case for put/get
  - Total complexity: O(k) + O(k) + O(k) + O(k) = O(k)

## Task 2 - EliminateStrategy

- `WordleWordList::eliminateWords`: O(m \* k)
  - **Main operation**: Iterates through all m possible words to filter valid candidates
  - **Per-word validation**: For each word, calls `WordleWord.isPossibleWord()` which has O(k) complexity:
    - Calls `WordleAnswer.matchWord()` to generate feedback: O(k)
    - Compares generated feedback with expected feedback using `.equals()`: O(k)
    - Adds valid words to new LinkedList: O(1) per addition
  - **Result construction**: Creates new LinkedList with remaining valid words
  - **Total complexity**: O(m \* k) - must check every possible word (m) with word-length operations (k)

## Task 3 - FrequencyStrategy

- `FrequencyStrategy::makeGuess`: O(m \* k)
  - **Word elimination**: Calls `eliminateWords()` to filter impossible words: O(m \* k)
  - **Probability calculation**: Calls `calculatePositionLetterProbabilities()`:
    - Iterates through k positions: O(k)
    - For each position, processes all m remaining words: O(m)
    - HashMap operations for counting and probability calculation: O(1) average case
    - Subtotal: O(k \* m)
  - **Best word selection**: Calls `getBestWordByExpectedGreenMatches()`:
    - Iterates through m possible answers: O(m)
    - For each word, calls `calculateExpectedGreenMatches()`: O(k)
      - Loops through k positions in the word: O(k)
      - For each position, performs HashMap lookup in `positionLetterProbabilities`: O(1) average case
      - Sums probabilities for each letter at its position: O(1) per position
      - Total per word: O(k)
    - Compares scores and tracks best word: O(1) per word
    - Subtotal: O(m \* k)
  - **Total complexity**: O(m \* k) + O(m \* k) + O(m \* k) = O(m \* k)

# Task 4 - Make your own (better) AI

For this task you do not need to give a runtime analysis.
Instead, you must explain your code. What was your idea for getting a better result? What is your strategy?

## Problem Essence

The problem we are trying to solve in essence is to find an element in a list with limited information, but we gain enough knowledge to be able to partition the list. In other words, we are trying to partition a list in the fewest amount of layers possible to find the one value we are searching for by either being the only value in the last partition or by having a high enough certainty.

Each guess we make provides feedback that allows us to eliminate portions of the search space, effectively partitioning the remaining possible answers into smaller groups. The goal is to minimize the number of partitioning steps (guesses) required to isolate the target answer or achieve sufficient confidence in our solution.

## Strategic Approaches

To approach this partitioning problem, we can implement a number of strategies. We can pursue the mathematically optimal solution of using the guess with highest information entropy each time, or we can use probability to find the letters with highest probability (letters are never uniformly used in languages) and guess those, etc.. In essence, the problem remains the same no matter what strategy: we are trying to eliminate the most answers possible with each guess before we eventually converge to the answer.

By ensuring that the worst-case outcome from a list of possible guesses is as favorable as possible, we essentially guarantee that each guess is at worst as good as it can be. This is the **maximin principle** - maximizing the minimum amount of removed answers from the search space for each guess in the worst-case scenario. However, this approach is extremely slow as it increases quadratically due to having to check each guess against all possible answers. This maximin idea is quite optimal, but due note: it is not as optimal as max information entropy. It is almost as good- it is however quite a lot faster computationally which is why it is chosen over information entropy. Even though maximin is quite nice it is still quite slow compared to linear time.

This computational challenge leads us to the idea of using a **two-phase system**: one phase which eliminates as much of the search space as possible quickly using linear-time operations, and then resorting to the more computationally heavy but more precise solution of maximin when the search space becomes manageable.

## Phase 1: Maximum Sum of Unique Letter Probabilities (Excluding Green Letters)

The linear phase of the program is elegantly simple yet mathematically sound. The core idea is to leverage the probability distribution of letters in the remaining possible answers while respecting already-known information.

### Mathematical Foundation

**Step 1: Letter Probability Calculation**

For each letter _l_ in the alphabet, we calculate its probability of appearing in the remaining possible answers:

```
P(l) = |{w ∈ W : l ∈ unique_letters(w, G)}| / |W|
```

Where:

- _W_ = set of remaining possible answer words
- _G_ = set of green (solved) positions
- _unique_letters(w, G)_ = unique letters in word _w_ excluding positions in _G_
- _|·|_ denotes set cardinality

**Step 2: Word Scoring Function**

For a candidate guess word _w_, the score is calculated as:

```
Score(w) = Σ P(l) for l ∈ {unique_letters(w, G) - L_green}
```

Where:

- _L_green_ = set of already-known green letters
- The sum includes each unique letter at most once per word

**Step 3: Optimization**

The optimal guess is selected by maximizing this score:

```
optimal_guess = argmax_{g ∈ GuessWords} Score(g)
```

Through repeated application of this probability-based strategy, we effectively reduce the search space from potentially thousands of candidates to a manageable subset. Once the remaining possible answers reach our threshold, the search space becomes sufficiently small for the computationally intensive minimax algorithm to operate within practical time constraints.

## Phase 2: Minimax Optimization (Worst-Case Minimization)

When the search space becomes sufficiently small (<=80 remaining possible answers), the strategy transitions to a minimax approach that guarantees optimal worst-case performance. This phase employs game-theoretic principles to minimize the maximum number of answers that could remain after any possible feedback pattern.

### Mathematical Foundation

**Step 1: Feedback Pattern Partitioning**

For a candidate guess _w_ and the set of remaining possible answers _A_, we first partition _A_ based on the feedback patterns that would result from each potential answer:

```
Partition(w, A) = {P₁, P₂, ...  Pₖ}
```

Where each partition _Pᵢ_ contains all answers that would produce the same feedback pattern when _w_ is guessed:

```
Pᵢ = {a ∈ A : pattern(match(w, a)) = pᵢ}
```

The function _pattern(feedback)_ converts WordleWord feedback into a standardized string representation (e.g., "GGYBY" for green-green-yellow-black-yellow).

**Step 2: Worst-Case Calculation**

For each candidate guess _w_, the worst-case remaining answers is the size of the largest partition:

```
WorstCase(w, A) = max{|Pᵢ| : Pᵢ ∈ Partition(w, A)}
```

This represents the maximum number of answers that could remain after receiving feedback, regardless of which answer is actually correct.

**Step 3: Minimax Optimization**

The optimal guess is selected by minimizing this worst-case value:

```
optimal_guess = argmin WorstCase(w, A) {w ∈ CandidateWords}
```

This follows the **minimax principle**: we minimize the maximum possible remaining search space, guaranteeing that no matter what the actual answer is, we will eliminate as many possibilities as possible.

**Step 4: Candidate Set Selection**

The algorithm adaptively chooses the candidate set based on search space size:

- **Very small space** (<=5 answers): _CandidateWords = possibleAnswers_ (possible answers only)
- **Small space** (6<->80 answers): \_CandidateWords = getGuessWordsList (full guess vocabulary)

This optimization recognizes that when very few answers remain, the optimal strategy is often to guess directly from the remaining possibilities rather than using external words for information gathering.

Due note: The values of <=5 and 6<->80 were found by manual guesstimating of decent hardcoded parameters.

### Psuedocode:

```
ALGORITHM MyStrategy

CONSTANTS:
    PROBABILITY_THRESHOLD = 80
    SMALL_SEARCH_SPACE_THRESHOLD = 5

INITIALIZE:
    dictionary <- given dictionary
    currentWords <- new WordleWordList(dictionary)
    greenSlots <- empty HashSet<Integer>
    greenLetters <- empty HashSet<Character>
    letterProbabilities <- empty HashMap<Character, Double>

FUNCTION MakeGuess(feedback):
    IF feedback != null THEN
        currentWords.EliminateWords(feedback)

        // Extract green information from feedback
        position <- 0
        FOR each character IN feedback DO
            IF character.answerType = CORRECT THEN
                greenSlots.Add(position)
                greenLetters.Add(ToLowerCase(character.letter))
            END IF
            position <- position + 1
        END FOR
    END IF

    // Calculate current letter probabilities
    letterProbabilities <- CalculateUniqueLetterOdds(currentWords.GetPossibleAnswers(), greenSlots)

    remainingAnswers <- currentWords.GetPossibleAnswers().Size()

    IF remainingAnswers > PROBABILITY_THRESHOLD THEN
        // PHASE 1: Probability-based exploration
        RETURN FindBestWordByProbability(dictionary.GetAnswerWordsList())
    ELSE IF remainingAnswers <= SMALL_SEARCH_SPACE_THRESHOLD THEN
        // PHASE 2a: Minimax with possible answers only
        RETURN FindBestWordByMinimax(currentWords.GetPossibleAnswers())
    ELSE
        // PHASE 2b: Minimax with full guess vocabulary
        RETURN FindBestWordByMinimax(dictionary.GetGuessWordsList())
    END IF
END FUNCTION

FUNCTION FindBestWordByProbability(candidateWords):
    bestWord <- candidateWords[0]
    bestScore <- CalculateProbabilityScore(bestWord)

    FOR each word IN candidateWords DO
        score <- CalculateProbabilityScore(word)
        IF score > bestScore THEN
            bestWord <- word
            bestScore <- score
        END IF
    END FOR

    RETURN bestWord
END FUNCTION

FUNCTION CalculateProbabilityScore(guess):
    score <- 0.0
    uniqueLettersScored <- empty HashSet<Character>

    FOR i = 0 TO guess.Length()-1 DO
        IF greenSlots.Contains(i) THEN
            CONTINUE  // Skip green positions
        END IF

        letter <- ToLowerCase(guess.CharAt(i))
        IF greenLetters.Contains(letter) THEN
            CONTINUE  // Skip known green letters
        END IF

        // Only score each unique letter once per word
        IF uniqueLettersScored.Add(letter) THEN
            score <- score + letterProbabilities.GetOrDefault(letter, 0.0)
        END IF
    END FOR

    RETURN score
END FUNCTION

FUNCTION CalculateUniqueLetterOdds(words, greenSlots):
    letterOdds <- empty HashMap<Character, Double>

    // Count occurrences of each unique letter
    FOR each word IN words DO
        uniqueLetters <- ExtractUniqueLettersExcludingPositions(word, greenSlots)
        FOR each letter IN uniqueLetters DO
            letterOdds.Put(letter, letterOdds.GetOrDefault(letter, 0.0) + 1)
        END FOR
    END FOR

    // Convert counts to probabilities
    FOR each (letter, count) IN letterOdds DO
        letterOdds.Put(letter, count / words.Size())
    END FOR

    RETURN letterOdds
END FUNCTION

FUNCTION ExtractUniqueLettersExcludingPositions(word, greenSlots):
    uniqueLetters <- empty HashSet<Character>
    FOR i = 0 TO word.Length()-1 DO
        IF NOT greenSlots.Contains(i) THEN
            uniqueLetters.Add(ToLowerCase(word.CharAt(i)))
        END IF
    END FOR
    RETURN uniqueLetters
END FUNCTION

FUNCTION FindBestWordByMinimax(candidateWords):
    possibleAnswers <- currentWords.GetPossibleAnswers()

    IF possibleAnswers.IsEmpty() THEN
        THROW IllegalStateException("No answers remaining")
    END IF

    IF possibleAnswers.Size() = 1 THEN
        RETURN possibleAnswers[0]
    END IF

    bestWord <- candidateWords[0]
    bestWorstCase <- INTEGER_MAX_VALUE

    FOR each guess IN candidateWords DO
        worstCase <- CalculateWorstCaseRemainingAnswers(guess, possibleAnswers)
        IF worstCase < bestWorstCase THEN
            bestWorstCase <- worstCase
            bestWord <- guess
        END IF
    END FOR

    RETURN bestWord
END FUNCTION

FUNCTION CalculateWorstCaseRemainingAnswers(guess, possibleAnswers):
    patternCounts <- empty HashMap<String, Integer>

    // Group answers by feedback pattern they would produce
    FOR each answer IN possibleAnswers DO
        pattern <- ConvertFeedbackToPattern(MatchWords(guess, answer))
        patternCounts.Merge(pattern, 1, Integer::sum)
    END FOR

    // Find the largest group (worst case)
    maxRemaining <- 0
    FOR each count IN patternCounts.Values() DO
        maxRemaining <- Max(maxRemaining, count)
    END FOR

    RETURN maxRemaining
END FUNCTION

FUNCTION ConvertFeedbackToPattern(feedback):
    pattern <- empty StringBuilder
    FOR each character IN feedback DO
        pattern.Append(character.answerType.character)
    END FOR
    RETURN pattern.ToString()
END FUNCTION
```

## Task 4 Runtime Analysis

For MyStrategy, the runtime analysis must consider both phases and their different complexities:

### Phase 1: Probability-Based Strategy

- `MakeGuess` (Phase 1 path): **O(m \* k + n \* k)**
  - **Word elimination**: `eliminateWords()` operation: O(m \* k)
  - **Letter probability calculation**: `CalculateUniqueLetterOdds()`: O(m \* k)
    - Iterates through m remaining possible answers: O(m)
    - For each word, calls `ExtractUniqueLettersExcludingPositions()`: O(k)
    - HashMap operations for counting: O(1) average case
    - Probability conversion: O(26) ≈ O(1) for alphabet size
    - Subtotal: O(m \* k)
  - **Best word selection**: `FindBestWordByProbability()`: O(n \* k)
    - Iterates through n answer words in dictionary: O(n)
    - For each word, calls `CalculateProbabilityScore()`: O(k)
      - Loops through k positions: O(k)
      - HashSet operations (contains, add): O(1) average case
      - HashMap lookup for probabilities: O(1) average case
      - Subtotal per word: O(k)
    - Total for best word selection: O(n \* k)
  - **Total Phase 1 complexity**: O(m \* k) + O(m \* k) + O(n \* k) = **O(m \* k + n \* k)**

### Phase 2: Minimax Strategy

- `MakeGuess` (Phase 2 path): **O(n \* m² \* k)** (worst case when using full guess vocabulary)
  - **Word elimination**: Same as Phase 1: O(m \* k)
  - **Letter probability calculation**: Same as Phase 1: O(m \* k)
  - **Minimax optimization**: `FindBestWordByMinimax()`: O(n \* m² \* k)
    - Candidate set selection depends on search space size:
      - If m ≤ 5: Uses m possible answers as candidates
      - If 5 < m ≤ 80: Uses full guess vocabulary (subset of n words)
    - Worst case: iterates through all n guess words: O(n)
    - For each candidate word, calls `CalculateWorstCaseRemainingAnswers()`: O(m² \* k)
      - Iterates through m possible answers: O(m)
      - For each answer, calls `MatchWords()` (from Task 1): O(k)
      - Calls `ConvertFeedbackToPattern()`: O(k)
      - HashMap merge operation: O(1) average case
      - Pattern generation subtotal per candidate: O(m \* k)
      - Groups answers by pattern, creates at most m different patterns: O(m)
      - Finding maximum count: O(m) iterations through pattern counts
      - Subtotal per candidate: O(m \* k + m) = O(m \* k)
      - **Note**: Total complexity becomes O(m² \* k) because pattern grouping requires O(m) additional work, and we do this for m answers
  - **Total Phase 2 complexity**: O(m \* k) + O(m \* k) + O(n \* m² \* k) = **O(n \* m² \* k)**

### Adaptive Complexity Analysis

The algorithm's runtime depends on the current state:

1. **Large search space** (m > 80): Uses Phase 1 with **O(m \* k + n \* k)**
2. **Medium search space** (5 < m ≤ 80): Uses Phase 2 with **O(n \* m² \* k)**
3. **Small search space** (m ≤ 5): Uses Phase 2 with **O(m³ \* k)** (since candidates = m)

### Practical Performance Characteristics

- **Early game** (high m): Linear complexity O(m \* k + n \* k) provides fast exploration
- **Mid game** (medium m): Quadratic complexity O(n \* m² \* k) becomes manageable as m decreases
- **End game** (low m): Cubic complexity O(m³ \* k) is negligible for small m

The threshold of 80 is chosen to balance computational cost with optimality: Phase 1 efficiently reduces large search spaces, while Phase 2 provides optimal decisions when computational cost becomes acceptable.

### Helper Method Complexities

- `ExtractUniqueLettersExcludingPositions()`: **O(k)** - single pass through word
- `CalculateProbabilityScore()`: **O(k)** - single pass through word with constant-time operations
- `ConvertFeedbackToPattern()`: **O(k)** - single pass through feedback
- `CalculateUniqueLetterOdds()`: **O(m \* k)** - processes all remaining words once

## Average Guess Analysis

### Performance5:

#### After 100 Wordle games the strategies got the following average guessing counts:

| Strategy          | Total Guesses | Games Won | Max Guesses in a Game |
| ----------------- | ------------- | --------- | --------------------- |
| RandomStrategy    | Too many      | -         | -                     |
| EliminateStrategy | 4,170         | 97/100    | 8                     |
| FrequencyStrategy | 3,850         | 98/100    | 7                     |
| MyStrategy        | 3,600         | 100/100   | 5                     |

### Performance ILL

#### After 100 Wordle games the strategies got the following average guessing counts:

| Strategy          | Total Guesses | Games Won | Max Guesses in a Game |
| ----------------- | ------------- | --------- | --------------------- |
| RandomStrategy    | Too many      | -         | -                     |
| EliminateStrategy | 6,680         | 63/100    | 19                    |
| FrequencyStrategy | 6,730         | 66/100    | 19                    |
| MyStrategy        | 3,610         | 94/100    | 8                     |
