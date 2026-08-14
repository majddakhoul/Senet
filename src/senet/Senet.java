package senet;

import ui.MainFrame;

/**
 * Application entry point. All interactive behaviour lives in the
 * {@code ui} package, built around {@link ui.MainFrame}; this class only
 * boots the Swing UI on the Event Dispatch Thread.
 */
public final class Senet {

    private Senet() {
    }

    public static void main(String[] args) {
        MainFrame.launch();
    }
}
