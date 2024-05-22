package leo;

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

public class Test extends JFrame {

    public Test() {
        setTitle("Casino");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set layout manager
        setLayout(new BorderLayout());

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load images
        ImageIcon rouletteIcon = null;
        ImageIcon blackjackIcon = null;
        rouletteIcon = new ImageIcon("C:\\Users\\danie\\Downloads\\Roulette.png");
		blackjackIcon = new ImageIcon("C:\\Users\\danie\\Downloads\\Blackjack Table.G03.watermarked.2k.png");

        // Create buttons
        JButton rouletteButton = new JButton("Roulette", rouletteIcon);
        JButton blackjackButton = new JButton("BlackJack", blackjackIcon);

        // Set button styles
        rouletteButton.setFont(new Font("Arial", Font.BOLD, 18));
        blackjackButton.setFont(new Font("Arial", Font.BOLD, 18));
        rouletteButton.setBackground(new Color(0, 153, 76));
        rouletteButton.setForeground(Color.WHITE);
        blackjackButton.setBackground(new Color(0, 153, 76));
        blackjackButton.setForeground(Color.WHITE);

        // Add buttons to panel
        buttonPanel.add(rouletteButton);
        buttonPanel.add(blackjackButton);

        // Create a label for the title
        JLabel titleLabel = new JLabel("Wilkommen in der Spielsucht, was spielen wir heute?", JLabel.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        titleLabel.setForeground(Color.WHITE);

        // Add title label and button panel to frame
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners for buttons
        rouletteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Roulette Button Clicked!");
            }
        });

        blackjackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BlackjackTable blackjackTable = new BlackjackTable();
                blackjackTable.setVisible(true);
            }
        });

        // Set the background color of the frame
        getContentPane().setBackground(new Color(34, 40, 49));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test().setVisible(true);
            }
        });
    }
}

class BlackjackTable extends JFrame {
    private final Deck deck;
    private final List<Card> playerHand;
    private final List<Card> dealerHand;
    private final TablePanel tablePanel;

    public BlackjackTable() {
        setTitle("Blackjack Table");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        // Deal initial cards
        dealInitialCards();

        // Setup UI
        tablePanel = new TablePanel();
        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton hitButton = new JButton("Hit");
        JButton standButton = new JButton("Stand");

        hitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerHand.add(deck.dealCard());
                tablePanel.repaint();
                checkPlayerBust();
            }
        });

        standButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dealerTurn();
                tablePanel.repaint();
                checkWinner();
            }
        });

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void dealInitialCards() {
        playerHand.add(deck.dealCard());
        playerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
    }

    private void dealerTurn() {
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(deck.dealCard());
        }
    }

    private int getHandValue(List<Card> hand) {
        int value = 0;
        int aceCount = 0;
        for (Card card : hand) {
            value += card.getValue();
            if (card.getRank() == Card.Rank.ACE) {
                aceCount++;
            }
        }
        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        return value;
    }

    private void checkPlayerBust() {
        if (getHandValue(playerHand) > 21) {
            JOptionPane.showMessageDialog(this, "You bust! Dealer wins.");
            resetGame();
        }
    }

    private void checkWinner() {
        int playerValue = getHandValue(playerHand);
        int dealerValue = getHandValue(dealerHand);
        String message;
        if (dealerValue > 21 || playerValue > dealerValue) {
            message = "You win!";
        } else if (playerValue == dealerValue) {
            message = "It's a tie!";
        } else {
            message = "Dealer wins!";
        }
        JOptionPane.showMessageDialog(this, message);
        resetGame();
    }

    private void resetGame() {
        deck.shuffle();
        playerHand.clear();
        dealerHand.clear();
        dealInitialCards();
        tablePanel.repaint();
    }

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