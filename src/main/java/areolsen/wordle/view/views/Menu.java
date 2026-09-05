package areolsen.wordle.view.views;

import areolsen.Main;
import areolsen.wordle.controller.AIController;
import areolsen.wordle.controller.HumanController;
import areolsen.wordle.model.Dictionary;
import areolsen.wordle.model.Board;
import areolsen.wordle.model.Model;
import areolsen.wordle.resources.LoadFromFile5LetterEnglish;
import areolsen.wordle.ai.strategy.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu implements ActionListener {
    private final JFrame frame;
    private JButton humanButton;
    private JButton eliminateButton;
    private JButton frequencyButton;
    private JButton randomButton;
    private JButton twoPhaseButton;

    private final Dictionary dictionary;
    private static final int MAX_GUESSES = 6;
    private static final int WORD_LENGTH = 5;

    private static final Color TEXT_COLOR = new Color(33,33,33);

    public Menu() {
        dictionary = new Dictionary(
            LoadFromFile5LetterEnglish.GUESS_WORDS_LIST,
            LoadFromFile5LetterEnglish.ANSWER_WORDS_LIST
        );

        frame = new JFrame();
        frame.setTitle(Main.WINDOW_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.WHITE);

        // Main panel with centered layout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 15, 0);

        // Title
        JLabel title = new JLabel("WORDLE");
        title.setFont(new Font("Monospaced", Font.BOLD, 48));
        title.setForeground(TEXT_COLOR);
        mainPanel.add(title, gbc);

        // Buttons
        gbc.gridy++;
        humanButton = createStyledButton("Human Player");
        mainPanel.add(humanButton, gbc);

        gbc.gridy++;
        eliminateButton = createStyledButton("Eliminate");
        mainPanel.add(eliminateButton, gbc);

        gbc.gridy++;
        frequencyButton = createStyledButton("Frequency");
        mainPanel.add(frequencyButton, gbc);

        gbc.gridy++;
        randomButton = createStyledButton("Random");
        mainPanel.add(randomButton, gbc);

        gbc.gridy++;
        twoPhaseButton = createStyledButton("Two Phase");
        mainPanel.add(twoPhaseButton, gbc);

        frame.add(mainPanel);
        frame.setSize(400, 550);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Monospaced", Font.PLAIN, 20));
        button.setForeground(TEXT_COLOR);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if (source == humanButton) {
            startGame(true, null);
        } else if (source == eliminateButton) {
            startGame(false, new EliminateStrategy(dictionary));
        } else if (source == frequencyButton) {
            startGame(false, new FrequencyStrategy(dictionary));
        } else if (source == randomButton) {
            startGame(false, new RandomStrategy(dictionary));
        } else if (source == twoPhaseButton) {
            startGame(false, new TwoPhase(dictionary));
        }
    }

    private void startGame(boolean humanController, Strategy strategy) {
        frame.dispose();
        Board board = new Board(MAX_GUESSES, WORD_LENGTH);
        Model model = new Model(board, dictionary);
        WordlePane view = new WordlePane(model);
        JFrame gameFrame = new JFrame(Main.WINDOW_TITLE);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setContentPane(view);
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(null);
        gameFrame.setVisible(true);
        if (humanController) {
            new HumanController(model, view);
        } else {
            new AIController(model, view, strategy);
        }
    }
}
