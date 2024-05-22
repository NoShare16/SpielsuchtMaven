package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

private class TablePanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Antialiasing for smooth edges
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background color
        g2d.setColor(new Color(34, 139, 34)); // Dark green
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw player cards
        g2d.setColor(Color.WHITE);
        g2d.drawString("Player's Hand:", 50, 350);
        drawHand(g2d, playerHand, 50, 370);

        // Draw dealer cards
        g2d.drawString("Dealer's Hand:", 50, 50);
        drawHand(g2d, dealerHand, 50, 70);
    }

    private void drawHand(Graphics2D g2d, List<Card> hand, int x, int y) {
        int cardWidth = 60;
        int cardHeight = 90;
        int spacing = 20;

        for (Card card : hand) {
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(x, y, cardWidth, cardHeight, 10, 10);
            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(x, y, cardWidth, cardHeight, 10, 10);
            g2d.drawString(card.toString(), x + 10, y + 50);
            x += cardWidth + spacing;
        }
    }

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        BlackjackTable table = new BlackjackTable();
        table.setVisible(true);
    });
}
}

class Card {
public enum Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(10), QUEEN(10), KING(10), ACE(11);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

private final Suit suit;
private final Rank rank;

public Card(Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
}

public Suit getSuit() {
    return suit;
}

public Rank getRank() {
    return rank;
}

public int getValue() {
    return rank.getValue();
}

@Override
public String toString() {
    return rank + " of " + suit;
}
}

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
