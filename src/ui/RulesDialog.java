package ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

/**
 * A simple modal dialog presenting the rules of the game (Kendall's Rules)
 * for players unfamiliar with Senet.
 */
public final class RulesDialog {

    private static final String RULES_TEXT =
            "SENET - HOW TO PLAY (Kendall's Rules)\n\n" +
            "Senet is played on 30 houses arranged in an S-shaped path of three\n" +
            "rows of ten. Each player has 7 pieces, placed alternately on the\n" +
            "first 14 houses. The goal is to be the first to move all 7 pieces\n" +
            "off the far end of the board.\n\n" +
            "THROWING STICKS\n" +
            "On your turn, four two-sided throwing sticks are cast. Each dark\n" +
            "side counts 1, each light side counts 0; the total (0-4) is your\n" +
            "move value, except a throw of 0 counts as a special 5.\n\n" +
            "MOVEMENT AND CAPTURE\n" +
            "Move one piece forward by exactly the rolled number of houses.\n" +
            "Landing on a house held by a single opposing piece swaps the two\n" +
            "pieces' positions. You may not land on a house held by your own\n" +
            "piece. A move cannot jump past House 26 (Happiness) - it must land\n" +
            "exactly on it. If you have no legal move for your roll, your turn\n" +
            "is forfeit.\n\n" +
            "SPECIAL HOUSES\n" +
            "  15  House of Rebirth      - the fallback square pieces return to.\n" +
            "  26  House of Happiness    - a mandatory checkpoint on the path.\n" +
            "  27  House of Water        - sends the piece straight back to the\n" +
            "                              House of Rebirth.\n" +
            "  28  House of Three Truths - can only leave with a roll of exactly\n" +
            "                              3, on the very next turn, or it is\n" +
            "                              sent back to the House of Rebirth.\n" +
            "  29  House of Re-Atoum     - the same rule as House 28, but the\n" +
            "                              required roll is exactly 2.\n" +
            "  30  House of Horus        - can leave with any roll, but only on\n" +
            "                              the very next turn, or it is sent\n" +
            "                              back to the House of Rebirth.\n\n" +
            "WINNING\n" +
            "The first player to move all seven pieces off the end of the board\n" +
            "wins the game.\n\n" +
            "See docs/GAME_RULES.md in the project for the full written reference.";

    private RulesDialog() {
    }

    public static void show(Frame owner) {
        JDialog dialog = new JDialog(owner, "How to Play", true);

        JTextArea textArea = new JTextArea(RULES_TEXT);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        textArea.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(560, 480));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(closeButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttons, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }
}
