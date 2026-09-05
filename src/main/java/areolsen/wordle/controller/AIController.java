package areolsen.wordle.controller;

import java.awt.event.KeyEvent;

import javax.swing.Timer;

import areolsen.wordle.ai.strategy.Strategy;
import areolsen.wordle.model.GameState;
import areolsen.wordle.model.word.Word;
import areolsen.wordle.view.views.WordlePane;
import areolsen.wordle.model.Model;

import java.awt.event.ActionEvent;

public class AIController extends AbstractController {
    private Strategy AI;
    private Word feedback;
    private Timer timer;

    public AIController(Model model, WordlePane view, Strategy strategy) {
        super(model, view);
        this.timer = new Timer(model.getTimerDelay(), this::clockTick);
        this.AI = strategy;
        view.addKeyListener(this);
        view.setFocusable(true);

        this.timer.start();
    }

    public void clockTick(ActionEvent e) {
        if (model.getGameState() == GameState.GAME_OVER)
            return;

        String guess = AI.makeGuess(feedback);
        for (int i = 0; i < guess.length(); i++) {
            char c = guess.charAt(i);
            model.addCharacter(c);
        }
        feedback = model.makeGuess();
        if (feedback.allMatch()){
            timer.stop();
        }
        timer.setDelay(model.getTimerDelay());
        model.clockTick();
        view.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_1) {
            AI.reset();
            model.reset();
            feedback = null;
            this.timer.start();
            view.repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

}
