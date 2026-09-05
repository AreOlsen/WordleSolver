package areolsen.wordle.controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import areolsen.wordle.model.Model;
import areolsen.wordle.view.views.WordlePane;

public abstract class AbstractController implements KeyListener {
    protected Model model;
    protected WordlePane view;

    public AbstractController(Model model, WordlePane view) {
        this.model = model;
        this.view = view;

        view.addKeyListener(this);
        view.setFocusable(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}
