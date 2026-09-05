package areolsen.wordle.model.word;

import java.util.Objects;

/**
 * This class represents a single Wordle character, and is comprised of a
 * Character and an AnswerType.
 */
public class Letter {
	public final Character letter;
	public final LetterAnswerType answerType;

	public Letter(Character letter, LetterAnswerType answerType) {
		this.letter = letter;
		this.answerType = answerType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(answerType, letter);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Letter other = (Letter) obj;
		return answerType == other.answerType && Objects.equals(letter, other.letter);
	}

}
