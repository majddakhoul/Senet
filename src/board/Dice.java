package board;

import java.io.Serializable;
import java.util.Random;

/**
 * Simulates the four two-sided throwing sticks used in Senet.
 * <p>
 * Each stick lands either dark side up (counted as 1) or light side up
 * (counted as 0). The four results are summed; a total of 0 (all light) is
 * a special throw worth 5. This yields the standard Senet probability
 * distribution: 1 -> 4/16, 2 -> 6/16, 3 -> 4/16, 4 -> 1/16, 5 -> 1/16.
 */
public class Dice implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final byte STICKS = 4;

    private final Random random;
    private byte lastRoll;

    public Dice() {
        this.random = new Random();
        this.lastRoll = 0;
    }

    /**
     * Throws the four sticks and returns the resulting move value (1-5).
     */
    public byte roll() {
        byte sum = 0;

        for (byte i = 0; i < STICKS; i++) {
            sum += random.nextInt(2);
        }

        if (sum == 0) {
            sum = 5;
        }

        lastRoll = sum;
        return lastRoll;
    }

    public byte getLastRoll() {
        return lastRoll;
    }

    /**
     * Returns the theoretical probability of a given roll value (1-5)
     * under the standard four-stick Senet distribution.
     */
    public static double getProbability(byte roll) {
        switch (roll) {
            case 1:
                return 4.0 / 16.0;
            case 2:
                return 6.0 / 16.0;
            case 3:
                return 4.0 / 16.0;
            case 4:
                return 1.0 / 16.0;
            case 5:
                return 1.0 / 16.0;
            default:
                return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Dice last roll: " + lastRoll;
    }
}
