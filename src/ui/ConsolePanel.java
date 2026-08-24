package ui;

import common.GameLog;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsolePanel extends JPanel implements GameLog {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final Color BACKGROUND = new Color(24, 26, 32);
    private static final Color TIMESTAMP_COLOR = new Color(120, 126, 140);
    private static final Color PLAYER_COLOR = new Color(96, 165, 250);
    private static final Color COMPUTER_COLOR = new Color(248, 148, 88);
    private static final Color SYSTEM_COLOR = new Color(190, 195, 205);
    private static final Color WIN_COLOR = new Color(110, 220, 140);
    private static final Color WARN_COLOR = new Color(235, 200, 90);

    private final JTextPane textPane;
    private final StyledDocument document;

    public ConsolePanel() {
        super(new BorderLayout());

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(BACKGROUND);
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        document = textPane.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        logSystem("Console ready. Every roll, move, and special-house effect will be reported here.");
    }

    @Override
    public void log(String message) {
        appendLine("GAME", SYSTEM_COLOR, message);
    }

    public void logPlayer(String message) {
        appendLine("PLAYER", PLAYER_COLOR, message);
    }

    public void logComputer(String message) {
        appendLine("COMPUTER", COMPUTER_COLOR, message);
    }

    public void logSystem(String message) {
        appendLine("SYSTEM", SYSTEM_COLOR, message);
    }

    public void logWarning(String message) {
        appendLine("NOTICE", WARN_COLOR, message);
    }

    public void logWin(String message) {
        appendLine("RESULT", WIN_COLOR, message);
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            try {
                document.remove(0, document.getLength());
            } catch (BadLocationException ignored) {

            }
        });
    }

    private void appendLine(String tag, Color color, String message) {
        Runnable task = () -> {
            SimpleAttributeSet timeStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(timeStyle, TIMESTAMP_COLOR);

            SimpleAttributeSet tagStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(tagStyle, color);
            StyleConstants.setBold(tagStyle, true);

            SimpleAttributeSet bodyStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(bodyStyle, color);

            try {
                document.insertString(document.getLength(), "[" + LocalTime.now().format(TIME_FORMAT) + "] ", timeStyle);
                document.insertString(document.getLength(), String.format("%-9s", tag), tagStyle);
                document.insertString(document.getLength(), message + "\n", bodyStyle);
            } catch (BadLocationException ignored) {

            }

            textPane.setCaretPosition(document.getLength());
        };

        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }
}
