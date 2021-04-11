package com.gamingutils.dice;

import lombok.*;

import java.util.Arrays;
import java.util.Random;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public final class DiceRoll {

    /**
     * When the dice was rolled what state was it in.
     */
    @RequiredArgsConstructor
    @Getter
    public enum RollState {
        NEUTRAL(""),
        ADVANTAGE("|+"),
        DISADVANTAGE("|-")
        ;
        private final String symbol;
    }

    @Getter
    private final int diceSides;
    @Getter
    private final int[] rollOutcome;
    @Getter
    private final int rollTotal;
    @Getter @ToString.Exclude
    private final int[][] rollResults;
    @Getter
    private final RollState state;

    public static DiceRoll roll(String diceString) throws InvalidDiceRoll {

        int advantage = 0, disadvantage = 0, rolled = 1, base;
        try {
            String[] split = diceString.split("d");
            if (split[0].length() > 0) {
                rolled = Integer.parseInt(split[0]);
            }
            split = split[1].split("\\[");
            if (split.length != 1) {
                split[1] = split[1].substring(0, split[1].length()-1);
                //I have advantage data
                String[] adData = split[1].split(",");
                if (adData.length != 0) {
                    String adv = adData[0].trim();
                    if (adv.length() != 0) {
                        advantage = Integer.parseInt(adv);
                    }
                    if (adData.length != 1) {
                        String dis = adData[1].trim();
                        if (dis.length() != 0) {
                            //I have disadvantage data
                            disadvantage = Integer.parseInt(dis);
                        }
                    }
                }
            }
            base = Integer.parseInt(split[0]);
        } catch (Exception e) {
            throw new InvalidDiceRoll(diceString);
        }

        return roll(rolled, base, advantage, disadvantage);
    }

    /**
     * Roll a dice.
     * @param number Number of dice to roll.
     * @param sides How many sides are on the dice that are rolled.
     * @param advantage Is there advantage.
     * @param disadvantage Is there disadvantage.
     * @return A DiceRoll Object containing the results of the roll.
     */
    public static DiceRoll roll(int number, int sides, int advantage, int disadvantage) throws InvalidDiceRoll {
        //Validate
        if (number < 1 || sides < 1 || advantage < 0 || disadvantage < 0)
            throw new InvalidDiceRoll(number, sides, advantage, disadvantage);

        //Setup
        int balance = advantage - disadvantage;
        RollState state = balance == 0 ? RollState.NEUTRAL : balance < 0 ? RollState.DISADVANTAGE : RollState.ADVANTAGE;
        balance = (balance < 0 ? balance * -1 : balance) + 1;
        int[][] rollResults = new int[number][balance];
        int[] rollOutcome = new int[number];
        Random diceRandom = new Random();

        //Calculate
        int total = 0;
        for (int i = 0; i < number; i++) {
            for (int o = 0; o < balance; o++) {
                rollResults[i][o] = diceRandom.nextInt(sides)+1;
                if (state == RollState.NEUTRAL || o == 0) {
                    rollOutcome[i] = rollResults[i][o];
                } else if (state == RollState.ADVANTAGE) {
                    rollOutcome[i] = Math.max(rollOutcome[i], rollResults[i][o]);
                } else {
                    rollOutcome[i] = Math.min(rollOutcome[i], rollResults[i][o]);
                }
            }
            total += rollOutcome[i];
        }

        //Return
        return new DiceRoll(sides, rollOutcome, total, rollResults, state);
    }


    public String getRoll() {
        return "[" + this.getRollTotal() + this.state.getSymbol() + "]";
    }

    public String getRollBreakdown() {
        String outcomes = Arrays.toString(this.rollOutcome);
        return this.getRoll() + "{" + outcomes.substring(1, outcomes.length()-1) + "}";
    }

    public String getRollVerbose() {
        String[] verboseRoll = new String[this.rollOutcome.length];
        for (int i = 0; i < verboseRoll.length; i++) {
            String rollLine = Arrays.toString(this.rollResults[i]);
            verboseRoll[i] = "(" + this.rollOutcome[i] + ")<" + rollLine.substring(1, rollLine.length()-1) + ">";
        }
        String outcomes = Arrays.toString(verboseRoll);
        return this.getRoll() + "{" + outcomes.substring(1, outcomes.length()-1) + "}";
    }

    public static final class InvalidDiceRoll extends Exception {

        /**
         * Thrown by a malformed dice string.<br/>
         * A dice string should be in the format of 'x'd'y'['a','b'] where:<br/>
         * - x is number of dice.<br/>
         * - y is sides on those dice.<br/>
         * - a is your advantage.<br/>
         * - b is your disadvantage.<br/>
         *
         * @param diceString The dice String attempting to be parsed.
         */
        InvalidDiceRoll(String diceString) {
            super("Malformed Dice String: " + diceString);
        }

        /**
         * Thrown when the values forming a dice do not make sense.
         * @param number Number of dice to throw. x >=1
         * @param sides Sides on those dice. y >= 1
         * @param advantage Advantage on the throw. a >= 0
         * @param disadvantage Disadvantage on the throw. b >= 0
         */
        InvalidDiceRoll(int number, int sides, int advantage, int disadvantage) {
            super("Unable to roll dice with the parameters: " + Arrays.toString(new int[]{number, sides, advantage, disadvantage}));
        }
    }
}
