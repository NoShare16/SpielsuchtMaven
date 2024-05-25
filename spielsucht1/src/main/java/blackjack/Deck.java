package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Deck {
    private final List<Card> cards;
    private int currentCardIndex;

    public Deck() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
        currentCardIndex = 0;
    }

    public Card dealCard() {
        if (currentCardIndex < cards.size()) {
            return cards.get(currentCardIndex++);
        } else {
            throw new IllegalStateException("No cards left in the deck");
        }
    }
}