package com.eric.blackjack;

import org.junit.jupiter.api.Test;

import javax.swing.plaf.synth.SynthSeparatorUI;

import static org.junit.jupiter.api.Assertions.*;

class SuitTest {

    @Test
    void heartPrintsHeart() {
        assertEquals("\u2665", Suit.HEARTS.toString());
    }

}