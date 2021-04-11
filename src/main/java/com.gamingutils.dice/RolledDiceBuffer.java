package com.gamingutils.dice;


import lombok.Data;
import lombok.Getter;
import net.objecthunter.exp4j.ExpressionBuilder;

@Data
public final class RolledDiceBuffer {

    @Getter
    private final DiceEvaluator rolledDice;
    @Getter
    private final DiceRoll[] rolls;
    @Getter
    private final int result;

    /**
     * Construct a rolled dice.
     * @param rolledDice Dice String to roll.
     * @throws DiceRoll.InvalidDiceRoll
     */
    RolledDiceBuffer(DiceEvaluator rolledDice) throws DiceRoll.InvalidDiceRoll {
        this.rolledDice = rolledDice;
        this.rolls = new DiceRoll[rolledDice.getDiceRolls().length];
        String[] formatData = new String[this.rolls.length];
        for (int i = 0; i < rolls.length; i++) {
            this.rolls[i] = DiceRoll.roll(rolledDice.getDiceRolls()[i]);
            formatData[i] = "" + this.rolls[i].getRollTotal();
        }
        this.result = (int) new ExpressionBuilder(String.format(rolledDice.getFormatString(),(Object[])formatData)).build().evaluate();
    }

    /**
     * Get the result with the input dice string.
     * @return String in the form of [&lt;result&gt;] = &lt;dice string&gt;
     */
    public String getEvaluationString() {
        String[] results = new String[this.rolls.length];
        for (int i = 0; i < this.rolls.length; i++) {
            results[i] = this.rolls[i].getRoll();
        }
        return "[" + this.result + "] = " + String.format(this.rolledDice.getFormatString(), (Object[]) results);
    }

    /**
     * Get the result with the input dice string, with a breakdown of what dice were rolled per dice roll request.
     * @return String in the form of [&lt;result&gt;] = &lt;dice string&gt;
     */
    public String getBreakdownEvaluationString() {
        String[] results = new String[this.rolls.length];
        for (int i = 0; i < this.rolls.length; i++) {
            results[i] = this.rolls[i].getRollBreakdown();
        }
        return "[" + this.result + "] = " + String.format(this.rolledDice.getFormatString(), (Object[]) results);
    }

    /**
     * Get the result with the input dice string, with a breakdown of what dice were rolled per dice roll request (including advantage/ disadvantage dice).
     * @return String in the form of [&lt;result&gt;] = &lt;dice string&gt;
     */
    public String getVerboseEvaluationString() {
        String[] results = new String[this.rolls.length];
        for (int i = 0; i < this.rolls.length; i++) {
            results[i] = this.rolls[i].getRollVerbose();
        }
        return "[" + this.result + "] = " + String.format(this.rolledDice.getFormatString(), (Object[]) results);
    }
}
