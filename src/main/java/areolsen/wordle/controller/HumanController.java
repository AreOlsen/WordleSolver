package areolsen.wordle.controller;

import java.awt.event.KeyEvent;

import areolsen.wordle.model.GameState;
import areolsen.wordle.model.Model;
import areolsen.wordle.view.views.WordlePane;


public class HumanController extends AbstractController {

    public HumanController(Model model, WordlePane view) {
        super(model, view);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Hit 1 to reset game
        if (e.getKeyCode() == KeyEvent.VK_1) {
            model.reset();
        }

        // No other actions are allowed when game is not active
        if (model.getGameState() != GameState.ACTIVE_GAME)
            return;

        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_BACK_SPACE)
            model.removeCharacter();
        else if (keyCode == KeyEvent.VK_ENTER) {
            try {
                model.makeGuess();
            } catch (IllegalArgumentException ex) {
                System.err.println(ex.getMessage());
            }
        }
        // Alphabet characters
        else if (keyCode >= 65 && keyCode <= 90)
            model.addCharacter((char) Character.toLowerCase(keyCode));

        view.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

}
