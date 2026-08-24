package common;

public interface GameLog {

    GameLog SILENT = message -> {
    };

    void log(String message);
}
