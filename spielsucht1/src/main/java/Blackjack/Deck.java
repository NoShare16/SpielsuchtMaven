package Blackjack;

import Blackjack.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;
    private int currentCard;

    public Deck() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        currentCard = 0;
    }

    public Card dealCard() {
        if (currentCard < cards.size()) {
            return cards.get(currentCard++);
        } else {
            shuffle();
            return cards.get(currentCard++);
        }
    }
}

