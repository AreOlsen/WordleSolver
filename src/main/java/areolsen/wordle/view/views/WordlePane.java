package areolsen.wordle.view.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import areolsen.grid.Cell;
import areolsen.grid.Position;
import areolsen.wordle.model.GameState;
import areolsen.wordle.model.word.LetterAnswerType;
import areolsen.wordle.view.graphics.ColorTheme;
import areolsen.wordle.view.graphics.DefaultColorTheme;
import areolsen.wordle.view.graphics.GraphicsHelper;
import areolsen.wordle.model.word.Letter;
import areolsen.wordle.model.Model;

public class WordlePane extends JPanel {
    private static final Dimension SCREEN_SIZE = new Dimension(400, 600);
    private static final Font font = new Font("Monospaced", Font.BOLD, 50);
    private static final double OUTER_MARGIN_RATIO = 0.08;
    private static final double INNER_MARGIN_RATIO = 0.015;
    private static final double CELL_ROUNDED_ARC = 0.12;
    private static final Color CELL_BORDER_COLOR = new Color(200, 200, 205);
    private Model model;
    private ColorTheme colors;

    public WordlePane(Model model) {
        this.model = model;
        this.colors = new DefaultColorTheme();
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.setPreferredSize(SCREEN_SIZE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GraphicsHelper.enableSmoothRendering(g2);

        drawGame(g2);
        if (this.model.getGameState() == GameState.GAME_OVER) {
            drawScreenText(g2, "GAME OVER");
        } else if (this.model.getGameState() == GameState.VICTORY) {
            drawScreenText(g2, "VICTORY");
        }
    }

    private void drawScreenText(Graphics2D g2, String text) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(
            0,
            0,
            getWidth(),
            getHeight()
        );
        g2.setColor(Color.WHITE);
        g2.setFont(font);
        GraphicsHelper.drawCenteredString(g2, text, getBounds());
    }

    private void drawGame(Graphics2D g2) {
        int width = this.getWidth();
        int height = this.getHeight();

        double outerMargin = Math.min(width, height) * OUTER_MARGIN_RATIO;
        double innerMargin = Math.min(width, height) * INNER_MARGIN_RATIO;

        int rows = model.getHeight();
        int cols = model.getWidth();

        double availableWidth = width - outerMargin * 2 - innerMargin * (cols + 1);
        double availableHeight = height - outerMargin * 2 - innerMargin * (rows + 1);
        double cellSize = Math.min(availableWidth / cols, availableHeight / rows);

        double startX = outerMargin + innerMargin + (availableWidth - cellSize * cols) / 2;
        double startY = outerMargin + innerMargin + (availableHeight - cellSize * rows) / 2;

        drawCells(g2, this.model.getTilesOnBoard(), startX, startY, cellSize, innerMargin);
        drawCells(g2, this.model.getCurrentGuess(), startX, startY, cellSize, innerMargin);
    }

    private void drawCells(Graphics2D g2, Iterable<Cell<Letter>> cells,
                          double startX, double startY, double cellSize, double innerMargin) {
        int cellFontSize = (int) (cellSize * 0.55);
        Font cellFont = new Font("Arial", Font.BOLD, cellFontSize);
        g2.setFont(cellFont);
        FontMetrics fm = g2.getFontMetrics();

        int arc = (int) (cellSize * CELL_ROUNDED_ARC);
        arc = Math.max(2, Math.min(arc, (int) (cellSize / 2)));

        for (Cell<Letter> cell : cells) {
            Position pos = cell.position();
            int gridX = pos.x();
            int gridY = pos.y();

            double x = startX + gridX * (cellSize + innerMargin);
            double y = startY + gridY * (cellSize + innerMargin);

            char textChar = cell.value().letter;
            LetterAnswerType colorChar = cell.value().answerType;
            String text = textChar + "";

            // Get color - if BLANK, use white background
            Color cellColor;
            if (colorChar == LetterAnswerType.BLANK) {
                cellColor = Color.WHITE;
            } else {
                cellColor = colors.getCellColor(colorChar);
            }

            // Draw cell background
            g2.setColor(cellColor);
            g2.fillRoundRect(
                (int) x,
                (int) y,
                (int) cellSize,
                (int) cellSize,
                arc,
                arc
            );

            // Draw cell border - thinner and lighter for blank cells
            if (colorChar == LetterAnswerType.BLANK) {
                g2.setColor(CELL_BORDER_COLOR);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
            } else {
                g2.setColor(CELL_BORDER_COLOR);
                g2.setStroke(new java.awt.BasicStroke(1.5f));
            }
            g2.drawRoundRect(
                (int) x,
                (int) y,
                (int) cellSize,
                (int) cellSize,
                arc,
                arc
            );

            // Draw letter - black for blank, white for filled
            if (colorChar == LetterAnswerType.BLANK) {
                Color black = new Color(50,50,55);
                g2.setColor(black);
            } else {
                g2.setColor(Color.WHITE);
            }

            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            int middleX = (int) (x + (cellSize - textWidth) / 2);
            int middleY = (int) (y + (cellSize + textHeight) / 2 - fm.getDescent());
            g2.drawString(text, middleX, middleY);
        }
    }
}
