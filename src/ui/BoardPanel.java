package ui;

import board.Board;
import board.Coordinate;
import board.House;
import board.HouseType;
import player.Piece;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Set;
import java.util.function.IntConsumer;

/**
 * Draws the 30-house S-shaped board and every piece on it, and reports
 * clicks back to whoever is listening by house number. Which houses (if
 * any) are currently clickable is entirely up to the caller, via
 * {@link #setHighlightedHouses(Set)}; this panel has no notion of whose
 * turn it is or what a legal move is.
 */
public class BoardPanel extends JPanel {

    private static final int COLUMNS = 10;
    private static final int ROWS = 3;
    private static final int CELL_SIZE = 74;
    private static final int MARGIN = 18;

    private static final Color BACKGROUND = new Color(30, 32, 38);
    private static final Color GRID_LINE = new Color(60, 63, 72);
    private static final Color NORMAL_HOUSE = new Color(235, 224, 200);
    private static final Color REBIRTH_HOUSE = new Color(178, 214, 178);
    private static final Color HAPPINESS_HOUSE = new Color(232, 200, 110);
    private static final Color WATER_HOUSE = new Color(150, 195, 224);
    private static final Color THREE_TRUTHS_HOUSE = new Color(200, 180, 224);
    private static final Color RE_ATOUM_HOUSE = new Color(210, 170, 200);
    private static final Color HORUS_HOUSE = new Color(232, 160, 110);
    private static final Color HIGHLIGHT = new Color(255, 215, 0);

    private static final Color PLAYER_B_FILL = new Color(48, 110, 200);
    private static final Color PLAYER_W_FILL = new Color(245, 245, 240);
    private static final Color PLAYER_B_TEXT = Color.WHITE;
    private static final Color PLAYER_W_TEXT = new Color(40, 40, 40);

    private Board board;
    private Set<Integer> highlightedHouses = Collections.emptySet();
    private IntConsumer clickHandler;

    public BoardPanel() {
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(
                MARGIN * 2 + COLUMNS * CELL_SIZE,
                MARGIN * 2 + ROWS * CELL_SIZE));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
    }

    public void setBoard(Board board) {
        this.board = board;
        repaint();
    }

    /**
     * Marks a set of house numbers (1-30) as clickable/highlighted, e.g.
     * the houses currently holding a movable piece. Pass an empty set to
     * clear all highlights.
     */
    public void setHighlightedHouses(Set<Integer> houses) {
        this.highlightedHouses = houses == null ? Collections.emptySet() : houses;
        repaint();
    }

    public void setClickHandler(IntConsumer clickHandler) {
        this.clickHandler = clickHandler;
    }

    private void handleClick(int x, int y) {
        if (board == null || clickHandler == null) {
            return;
        }

        int col = (x - MARGIN) / CELL_SIZE;
        int row = (y - MARGIN) / CELL_SIZE;

        if (col < 0 || col >= COLUMNS || row < 0 || row >= ROWS) {
            return;
        }

        int houseNumber = new Coordinate((byte) col, (byte) row).toHouseNumber();
        clickHandler.accept(houseNumber);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (board == null) {
            drawPlaceholder(g2);
            return;
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                int houseNumber = new Coordinate((byte) col, (byte) row).toHouseNumber();
                drawHouse(g2, row, col, board.getHouse(houseNumber));
            }
        }
    }

    private void drawPlaceholder(Graphics2D g2) {
        g2.setColor(GRID_LINE);
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        String message = "Start a New Game to see the board.";
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2.drawString(message, Math.max(x, 10), y);
    }

    private void drawHouse(Graphics2D g2, int row, int col, House house) {
        int x = MARGIN + col * CELL_SIZE;
        int y = MARGIN + row * CELL_SIZE;
        int size = CELL_SIZE - 6;

        g2.setColor(colorForHouseType(house.getType()));
        g2.fillRoundRect(x, y, size, size, 10, 10);

        boolean highlighted = highlightedHouses.contains((int) house.getNumber());
        g2.setStroke(new BasicStroke(highlighted ? 3f : 1f));
        g2.setColor(highlighted ? HIGHLIGHT : GRID_LINE);
        g2.drawRoundRect(x, y, size, size, 10, 10);

        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g2.setColor(new Color(70, 70, 70));
        g2.drawString(String.valueOf(house.getNumber()), x + 4, y + 12);

        if (house.getType() != HouseType.NORMAL) {
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            FontMetrics fm = g2.getFontMetrics();
            String label = String.valueOf(house.getType().getSymbol());
            g2.drawString(label, x + size - fm.stringWidth(label) - 4, y + 12);
        }

        if (!house.isEmpty()) {
            drawPiece(g2, x, y, size, house.getOccupiedBy(), highlighted);
        }
    }

    private void drawPiece(Graphics2D g2, int x, int y, int size, Piece piece, boolean highlighted) {
        int diameter = size - 20;
        int px = x + (size - diameter) / 2;
        int py = y + (size - diameter) / 2 + 6;

        boolean isBlue = piece.getColor() == 'B';
        Color fill = isBlue ? PLAYER_B_FILL : PLAYER_W_FILL;
        Color textColor = isBlue ? PLAYER_B_TEXT : PLAYER_W_TEXT;

        if (highlighted) {
            g2.setColor(HIGHLIGHT);
            g2.fillOval(px - 4, py - 4, diameter + 8, diameter + 8);
        }

        g2.setColor(fill);
        g2.fillOval(px, py, diameter, diameter);
        g2.setColor(new Color(30, 30, 30));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(px, py, diameter, diameter);

        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        g2.setColor(textColor);
        String label = String.valueOf(piece.getId());
        FontMetrics fm = g2.getFontMetrics();
        int tx = px + (diameter - fm.stringWidth(label)) / 2;
        int ty = py + (diameter + fm.getAscent()) / 2 - 2;
        g2.drawString(label, tx, ty);
    }

    private Color colorForHouseType(HouseType type) {
        switch (type) {
            case REBIRTH:
                return REBIRTH_HOUSE;
            case HAPPINESS:
                return HAPPINESS_HOUSE;
            case WATER:
                return WATER_HOUSE;
            case THREE_TRUTHS:
                return THREE_TRUTHS_HOUSE;
            case RE_ATOUM:
                return RE_ATOUM_HOUSE;
            case HORUS:
                return HORUS_HOUSE;
            default:
                return NORMAL_HOUSE;
        }
    }
}
