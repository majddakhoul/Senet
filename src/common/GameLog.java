package common;

/**
 * Callback used by {@code gameLogic.GameLogic} to narrate what happens on
 * the board (rolls, moves, captures, special-house effects) without taking
 * any dependency on a particular UI. The Swing console panel implements
 * this to show messages on screen; the AI search uses the no-op
 * implementation ({@link #SILENT}) so that hypothetical moves it merely
 * considers are never shown to the player.
 */
public interface GameLog {

    /**
     * A logger that discards every message. Used while the AI explores
     * hypothetical future states during search.
     */
    GameLog SILENT = message -> {
    };

    void log(String message);
}
