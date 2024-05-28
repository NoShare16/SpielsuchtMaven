package blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of playing cards for a game of blackjack.
 */
class Deck {
    private final List<Card> cards;
    private int currentCardIndex;

    /**
     * Constructs a new deck of 52 playing cards and shuffles it.
     */
    public Deck() {
        cards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    /**
     * Shuffles the deck of cards and resets the current card index.
     */
    public void shuffle() {
        Collections.shuffle(cards);
        currentCardIndex = 0;
    }

    /**
     * Deals the next card from the deck.
     * 
     * @return the next card in the deck
     * @throws IllegalStateException if no cards are left in the deck
     */
    public Card dealCard() {
        if (currentCardIndex < cards.size()) {
            return cards.get(currentCardIndex++);
        } else {
            throw new IllegalStateException("No cards left in the deck");
        }
    }
}
