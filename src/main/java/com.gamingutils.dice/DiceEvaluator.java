package com.gamingutils.dice;

import lombok.Getter;
import lombok.SneakyThrows;

public final class DiceEvaluator {

    private static final String NUMBERS = "0123456789";

    @Getter
    private final String rollable;
    @Getter
    private final String[] diceRolls;
    @Getter
    private final String formatString;

    /**
     * Create a Object that evaluates a dice String for rolling.
     * @param rollable String formatted dice roll request.
     */
    public DiceEvaluator(String rollable) {
        this.rollable = rollable.toLowerCase();
        int dice = 0;
        for (char c : rollable.toCharArray()) if (c == 'd') dice++;
        this.diceRolls = new String[dice];

        String formatBuilder = this.rollable;
        int start = 0, end = 0, insert = 0;
        for (int i = 0; i < formatBuilder.length(); i++) {
            if (formatBuilder.charAt(i) == 'd') {
                //Search Forwards
                boolean foundEnd = false;
                for (int o = i+1; o < formatBuilder.length(); o++) {
                    if (!NUMBERS.contains("" + formatBuilder.charAt(o))) {
                        if (formatBuilder.charAt(o) == '[') {
                            while (formatBuilder.charAt(o++) != ']');
                        }
                        end = o;
                        foundEnd = true;
                        break;
                    }
                }
                if (!foundEnd) {
                    end = formatBuilder.length();
                }
                //Search Backwards
                for (int o = i-1; o > -1; o--) {
                    if (!NUMBERS.contains("" + formatBuilder.charAt(o))) {
                        start = o+1;
                        break;
                    }
                }
                i = start + 1;
                this.diceRolls[insert] = formatBuilder.substring(start, end).trim();
                formatBuilder = formatBuilder.substring(0, start) + "%s" + formatBuilder.substring(end);
                insert++;
            }
            if (insert == this.diceRolls.length) break;
        }
        this.formatString = formatBuilder;
    }

    /**
     * Roll the dice from this evaluator.
     * @return Rolled dice result.
     */
    @SneakyThrows
    public RolledDiceBuffer roll() {
        return new RolledDiceBuffer(this);
    }
}
