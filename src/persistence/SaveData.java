package persistence;

import gameLogic.Difficulty;
import gameLogic.State;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SaveData implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final State state;
    private final Difficulty difficulty;
    private final int aiDepth;
    private final boolean debugMode;
    private final String savedAt;

    public SaveData(State state, Difficulty difficulty, int aiDepth, boolean debugMode) {
        this.state = state;
        this.difficulty = difficulty;
        this.aiDepth = aiDepth;
        this.debugMode = debugMode;
        this.savedAt = LocalDateTime.now().format(TIMESTAMP_FORMAT);
    }

    public State getState() {
        return state;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getAiDepth() {
        return aiDepth;
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public String getSavedAt() {
        return savedAt;
    }
}
