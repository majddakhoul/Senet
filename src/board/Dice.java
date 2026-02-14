package board;

import java.util.Random;

public class Dice {

    private static final byte STICKS = 4;

    private Random random;
    private byte lastRoll;

    public Dice() {
        this.random = new Random();
        this.lastRoll = 0;
    }

    public byte roll() {

        byte sum = 0;

        for (byte i = 0; i < STICKS; i++) {
            sum += random.nextInt(2);
        }

        if (sum == 0)
            sum = 5;

        lastRoll = sum;
        return lastRoll;
    }

    public byte getLastRoll() {
        return lastRoll;
    }

    public static double getProbability(byte roll) {

        switch (roll) {
            case 1: return 4.0 / 16.0;
            case 2: return 6.0 / 16.0;
            case 3: return 4.0 / 16.0;
            case 4: return 1.0 / 16.0;
            case 5: return 1.0 / 16.0;
            default: return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Dice last roll: " + lastRoll;
    }
}
