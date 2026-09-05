package areolsen.wordle.model;

import java.util.ArrayList;
import java.util.List;

import areolsen.grid.Position;
import areolsen.grid.Cell;

import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.model.word.Answer;
import areolsen.wordle.model.word.Word;

public class Model {
    private Board board;
    private Dictionary dictionary;
    private Answer answer;
    private String currentGuess;

    private GameState gameState;

    public Model(Board board) {
        this(board, new Dictionary());
    }

    public Model(Board board, Dictionary dictionary) {
        this.board = board;
        this.dictionary = dictionary;
        this.answer = new Answer(dictionary);
        this.currentGuess = "";

        this.gameState = GameState.ACTIVE_GAME;
    }

    public boolean removeCharacter() {
    	if(currentGuess.isEmpty())
    		return false;

    	currentGuess = currentGuess.substring(0, currentGuess.length()-1);
        return true;
    }

    public boolean addCharacter(char c) {
    	if(currentGuess.length()>= dictionary.WORD_LENGTH)
    		return false;
    	currentGuess = currentGuess+c;
        return true;
    }

    public Word makeGuess() throws IllegalArgumentException {
        if (!dictionary.isLegalGuess(currentGuess))
            throw new IllegalArgumentException("Word is not legal");

        // Check what letters were CORRECT/WRONG POSITION/WRONG
        Word guessFeedback = answer.makeGuess(currentGuess);
        board.setRow(guessFeedback);
        if (guessFeedback.allMatch())
            gameState = GameState.VICTORY;
        else if (board.getCurrentRow()+1 > board.rows())
            gameState = GameState.GAME_OVER;

        currentGuess = "";
        return guessFeedback;
    }

    public Iterable<Cell<Letter>> getTilesOnBoard() {
        return board;
    }
    public Iterable<Cell<Letter>> getCurrentGuess() {
        List<Cell<Letter>> cellList = new ArrayList<>();
        int col = 0;
        for (Character c : currentGuess.toCharArray()) {
        	Letter cg = new Letter(c, LetterAnswerType.BLANK);
            Position pos = new Position(col++,board.getCurrentRow());
            cellList.add(new Cell<Letter>(pos, cg));
        }
        return cellList;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public GameState getGameState() {
        return gameState;
    }

    public int getTimerDelay() {
        return 1000;
    }

    public void clockTick() {
        if (gameState == GameState.GAME_OVER)
            return;
    }

    public int getHeight(){
        return board.rows();
    }

    public int getWidth(){
        return board.cols();
    }

    public void reset() {
        this.answer = new Answer(dictionary);
        this.currentGuess = "";
        this.board = new Board(this.board.rows(), this.board.cols());

        this.gameState = GameState.ACTIVE_GAME;
    }

}
