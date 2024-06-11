package com.eric.blackjack;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void canGetValueOfATwoCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.TWO);
        assertEquals(2, card1.getValue());
    }

    @Test
    void canGetValueOfAThreeCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.THREE);
        assertEquals(3, card1.getValue());
    }

    @Test
    void canGetValueOfAAceCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.ACE);
        assertEquals(1, card1.getValue());
    }

    @Test
    void canGetValueOfAJackCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.JACK);
        assertEquals(10, card1.getValue());
    }

    @Test
    void canGetValueOfAQueenCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.QUEEN);
        assertEquals(10, card1.getValue());
    }

    @Test
    void canGetValueOfAKingCard() {
        Card card1 = new Card(Suit.DIAMONDS, Rank.KING);
        assertEquals(10, card1.getValue());
    }
}