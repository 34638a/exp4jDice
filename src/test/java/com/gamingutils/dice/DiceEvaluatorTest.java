package com.gamingutils.dice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DiceEvaluatorTest {

    private static final String[] rolls = new String[] {
            "", "1", "2", "3"
    };

    private static final String[] diceRolls = new String[] {
            "%sd4", "%sd6", "%sd8", "%sd10", "%sd12", "%sd20", "%sd100",
    };

    private static final String[] balanceStates = new String[] {
            "", " ", "1", " 1", "1 ", " 1 "
    };

    private static final String[] balanceBrackets = new String[] {
            "", "[]", "[%s]", "[,%s]", "[%s,%s]"
    };

    private static String[] balancePermutations = createBalancePermutations();

    private static String[] createBalancePermutations() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(balanceBrackets[0]);
        strings.add(balanceBrackets[1]);
        for (int i = 0; i < balanceStates.length; i++) {
            strings.add(String.format(balanceBrackets[2], balanceStates[i]));
            strings.add(String.format(balanceBrackets[3], balanceStates[i]));
            for (int o = 0; o < balanceStates.length; o++) {
                strings.add(String.format(balanceBrackets[4], balanceStates[i], balanceStates[o]));
            }
        }

        return strings.toArray(new String[0]);
    }

    private static String[] dicePermutations = createDicePermutations();

    private static String[] createDicePermutations() {
        ArrayList<String> dice = new ArrayList<>();
        for (String die : diceRolls) {
            for (int i = 0; i < rolls.length; i++) {
                dice.add(String.format(die, rolls[i]));
            }
        }

        return dice.toArray(new String[0]);
    }

    private static String[] validDice = createValidDice();

    private static String[] createValidDice() {
        ArrayList<String> validDice = new ArrayList<>();

        for (String dice : dicePermutations) {
            for (String balance : balancePermutations) {
                validDice.add(dice + balance);
            }
        }

        balancePermutations = null;
        dicePermutations = null;
        return validDice.toArray(new String[0]);
    }

    /**
     * Yes I am aware that this is a bad unit test but the amount of test strings grows quickly based off what is a valid dice.
     */
    @Test
    public void validDiceStringsTest() {

        boolean failed = false;
        for (String roll : validDice) {
            try {
                new DiceEvaluator(roll).roll();
            } catch (Exception e) {
                failed = true;
                System.out.println("Unable to evaluated dice: " + roll);
            }
        }
        assertTrue(!failed);
    }
}