package com.eric.game;

import java.util.Random;

public class GuessingGame {

    private final int randomNumber = new Random().nextInt(10) + 1;
    private int counter=0;

    public String guess(int guessNumber) {
        counter++;
        String tryText = (counter > 1) ? "tries" : "try";
        String loseMsg = String.format("You didn't get it in %d %s. Game over", counter, tryText);
        String winningMsg = String.format("You got it in %d %s", counter, tryText);
        return guessNumber == getRandomNumber() ? winningMsg : loseMsg;
    }

    public int getRandomNumber() {
        return randomNumber;
    }
}
