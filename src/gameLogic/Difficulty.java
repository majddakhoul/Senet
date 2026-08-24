package gameLogic;

import java.io.Serializable;

public enum Difficulty implements Serializable {

    EASY(2, "Easy — the computer looks two half-moves ahead."),
    MEDIUM(4, "Medium — a balanced, moderately challenging opponent."),
    HARD(6, "Hard — a strong opponent that plans several moves ahead."),
    EXPERT(8, "Expert — the deepest search; expect a tough, slow-thinking game."),
    CUSTOM(4, "Custom — you choose the exact search depth.");

    private final int defaultDepth;
    private final String description;

    Difficulty(int defaultDepth, String description) {
        this.defaultDepth = defaultDepth;
        this.description = description;
    }

    public int getDefaultDepth() {
        return defaultDepth;
    }

    public String getDescription() {
        return description;
    }
}
