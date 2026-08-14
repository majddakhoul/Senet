package board;

import java.io.Serializable;

/**
 * The type of a single square ("house") on the Senet board.
 * <p>
 * A regular square has no special behaviour. The final five squares of the
 * board (26-30) each carry a unique rule, implemented in
 * {@code gameLogic.GameLogic}.
 */
public enum HouseType implements Serializable {

    NORMAL("Normal", '-'),
    REBIRTH("House of Rebirth", 'R'),
    HAPPINESS("House of Happiness", 'H'),
    WATER("House of Water", 'W'),
    THREE_TRUTHS("House of Three Truths", 'T'),
    RE_ATOUM("House of Re-Atoum", 'A'),
    HORUS("House of Horus", 'O');

    private final String displayName;
    private final char symbol;

    HouseType(String displayName, char symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    /**
     * Resolves the {@link HouseType} that a 1-based house number (1-30)
     * corresponds to on a standard Kendall's Rules Senet board.
     */
    public static HouseType forHouseNumber(int houseNumber) {
        switch (houseNumber) {
            case 15:
                return REBIRTH;
            case 26:
                return HAPPINESS;
            case 27:
                return WATER;
            case 28:
                return THREE_TRUTHS;
            case 29:
                return RE_ATOUM;
            case 30:
                return HORUS;
            default:
                return NORMAL;
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public char getSymbol() {
        return symbol;
    }
}
