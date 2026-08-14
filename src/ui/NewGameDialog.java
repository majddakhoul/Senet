package ui;

import gameLogic.Difficulty;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;

/**
 * Modal setup wizard shown before a new game starts: the player's name, a
 * difficulty preset (or a custom search depth), and whether to enable
 * verbose AI search logging.
 */
public class NewGameDialog extends JDialog {

    /** The choices made in the dialog, or {@code null} if it was cancelled. */
    public static final class Result {
        public final String playerName;
        public final Difficulty difficulty;
        public final int searchDepth;
        public final boolean debugLogging;

        private Result(String playerName, Difficulty difficulty, int searchDepth, boolean debugLogging) {
            this.playerName = playerName;
            this.difficulty = difficulty;
            this.searchDepth = searchDepth;
            this.debugLogging = debugLogging;
        }
    }

    private static final int MIN_DEPTH = 1;
    private static final int MAX_DEPTH = 8;
    private static final int DEFAULT_DEPTH = 4;

    private final JTextField nameField = new JTextField("Player", 16);
    private final JComboBox<Difficulty> difficultyBox = new JComboBox<>(Difficulty.values());
    private final JSpinner depthSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_DEPTH, MIN_DEPTH, MAX_DEPTH, 1));
    private final JCheckBox debugCheck = new JCheckBox("Verbose AI search logging (printed to the terminal)");
    private final JLabel descriptionLabel = new JLabel(" ");

    private Result result;

    private NewGameDialog(Frame owner) {
        super(owner, "New Game", true);
        buildUi();
    }

    /**
     * Shows the dialog modally and returns the player's choices, or
     * {@code null} if they cancelled.
     */
    public static Result showDialog(Frame owner) {
        NewGameDialog dialog = new NewGameDialog(owner);
        dialog.setVisible(true);
        return dialog.result;
    }

    private void buildUi() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        form.add(new JLabel("Your name:"), c);
        c.gridx = 1;
        form.add(nameField, c);

        c.gridx = 0;
        c.gridy = 1;
        form.add(new JLabel("Difficulty:"), c);
        c.gridx = 1;
        form.add(difficultyBox, c);

        c.gridx = 0;
        c.gridy = 2;
        form.add(new JLabel("Search depth:"), c);
        c.gridx = 1;
        form.add(depthSpinner, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        form.add(descriptionLabel, c);

        c.gridy = 4;
        form.add(debugCheck, c);

        difficultyBox.addActionListener(e -> updateForSelectedDifficulty());
        updateForSelectedDifficulty();

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener(e -> onStart());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            result = null;
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(startButton);

        setLayout(new java.awt.BorderLayout());
        add(form, java.awt.BorderLayout.CENTER);
        add(buttons, java.awt.BorderLayout.SOUTH);

        getRootPane().setDefaultButton(startButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
    }

    private void updateForSelectedDifficulty() {
        Difficulty selected = (Difficulty) difficultyBox.getSelectedItem();
        descriptionLabel.setText("<html><i>" + selected.getDescription() + "</i></html>");

        boolean custom = selected == Difficulty.CUSTOM;
        depthSpinner.setEnabled(custom);

        if (!custom) {
            depthSpinner.setValue(Math.min(selected.getDefaultDepth(), MAX_DEPTH));
        }
    }

    private void onStart() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        Difficulty selected = (Difficulty) difficultyBox.getSelectedItem();
        int depth = (Integer) depthSpinner.getValue();

        result = new Result(name, selected, depth, debugCheck.isSelected());
        dispose();
    }
}
