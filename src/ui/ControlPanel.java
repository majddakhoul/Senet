package ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class ControlPanel extends JPanel {

    private final JLabel turnLabel = new JLabel("No game in progress");
    private final JLabel rollLabel = new JLabel(" ");
    private final JLabel countsLabel = new JLabel(" ");
    private final JLabel difficultyLabel = new JLabel(" ");

    private final JButton rollButton = new JButton("Roll Dice");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton saveButton = new JButton("Save Game");
    private final JButton loadButton = new JButton("Load Game");
    private final JButton newGameButton = new JButton("New Game");
    private final JButton rulesButton = new JButton("Rules");

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setPreferredSize(new Dimension(230, 0));

        addSectionTitle("Status");
        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.BOLD, 15f));
        add(turnLabel);
        add(Box.createVerticalStrut(4));
        add(rollLabel);
        add(Box.createVerticalStrut(4));
        add(countsLabel);
        add(Box.createVerticalStrut(4));
        add(difficultyLabel);

        add(Box.createVerticalStrut(18));
        addSectionTitle("Actions");
        add(button(rollButton));
        add(Box.createVerticalStrut(6));
        add(button(pauseButton));
        add(Box.createVerticalStrut(6));
        add(button(saveButton));
        add(Box.createVerticalStrut(6));
        add(button(loadButton));
        add(Box.createVerticalStrut(6));
        add(button(newGameButton));
        add(Box.createVerticalStrut(6));
        add(button(rulesButton));

        add(Box.createVerticalGlue());

        setGameInProgress(false);
    }

    private void addSectionTitle(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        label.setForeground(new Color(120, 120, 120));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(label);
        add(Box.createVerticalStrut(6));
    }

    private JButton button(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        return b;
    }

    public void attachController(GameController controller) {
        rollButton.addActionListener(e -> controller.rollDice());
        pauseButton.addActionListener(e -> controller.togglePause());
        saveButton.addActionListener(e -> controller.promptSave());
        loadButton.addActionListener(e -> controller.promptLoad());
        newGameButton.addActionListener(e -> controller.promptNewGame());
        rulesButton.addActionListener(e -> controller.showRules());
    }

    public void setTurnText(String text) {
        turnLabel.setText(text);
    }

    public void setRollText(String text) {
        rollLabel.setText(text);
    }

    public void setCountsText(String text) {
        countsLabel.setText("<html>" + text + "</html>");
    }

    public void setDifficultyText(String text) {
        difficultyLabel.setText("<html>" + text + "</html>");
    }

    public void setRollEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
    }

    public void setPauseButtonText(String text) {
        pauseButton.setText(text);
    }

    public void setPauseEnabled(boolean enabled) {
        pauseButton.setEnabled(enabled);
    }

    public void setSaveEnabled(boolean enabled) {
        saveButton.setEnabled(enabled);
    }

    public void setGameInProgress(boolean inProgress) {
        pauseButton.setEnabled(inProgress);
        saveButton.setEnabled(inProgress);
        if (!inProgress) {
            rollButton.setEnabled(false);
            turnLabel.setText("No game in progress");
            rollLabel.setText(" ");
            countsLabel.setText(" ");
            difficultyLabel.setText(" ");
        }
    }
}
