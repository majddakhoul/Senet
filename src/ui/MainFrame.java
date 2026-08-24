package ui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Senet");

        BoardPanel boardPanel = new BoardPanel();
        ConsolePanel consolePanel = new ConsolePanel();
        ControlPanel controlPanel = new ControlPanel();

        GameController controller = new GameController(boardPanel, consolePanel, controlPanel, this);

        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.add(boardPanel, BorderLayout.CENTER);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, boardContainer, consolePanel);
        centerSplit.setResizeWeight(0.62);
        centerSplit.setContinuousLayout(true);

        setLayout(new BorderLayout());
        add(centerSplit, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        setJMenuBar(buildMenuBar(controller));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 560));
        pack();
        setLocationRelativeTo(null);
    }

    private JMenuBar buildMenuBar(GameController controller) {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        addItem(gameMenu, "New Game...", e -> controller.promptNewGame());
        addItem(gameMenu, "Save Game...", e -> controller.promptSave());
        addItem(gameMenu, "Load Game...", e -> controller.promptLoad());
        gameMenu.addSeparator();
        addItem(gameMenu, "Exit", e -> System.exit(0));

        JMenu helpMenu = new JMenu("Help");
        addItem(helpMenu, "How to Play...", e -> controller.showRules());

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void addItem(JMenu menu, String label, java.awt.event.ActionListener listener) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(listener);
        menu.add(item);
    }

    public static void launch() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {

        }

        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
