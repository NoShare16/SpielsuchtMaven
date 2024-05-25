package blackjack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Blackjack game table with a graphical user interface.
 */
public class BlackjackTable extends JFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Deck deck;
    private final List<Card> playerHand;
    private final List<Card> dealerHand;
    private final TablePanel tablePanel;
    private boolean doubledDown = false; // Track if double down was used

    private final List<Card> splitHand; // Second hand for split
    private boolean isSplit = false; // Track if split occurred

    // Map to store card images
    private final Map<String, BufferedImage> cardImages;

    // Button references
    private JPanel buttonPanel;
    private JButton hitButton;
    private JButton standButton;
    private JButton doubleDownButton;
    private JButton splitButton;
    private JButton hitHand1Button;
    private JButton hitHand2Button;
    private JButton doubleDownHand1Button;
    private JButton doubleDownHand2Button;

    /**
     * Constructs the Blackjack table and initializes the game components.
     */
    public BlackjackTable() {
        setTitle("Blackjack Table");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        splitHand = new ArrayList<>();

        // Initialize the card images map
        cardImages = loadCardImages();

        // Deal initial cards
        dealInitialCards();

        // Setup UI
        tablePanel = new TablePanel();
        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        hitButton = new JButton("Hit");
        standButton = new JButton("Stand");
        doubleDownButton = new JButton("Double Down");
        splitButton = new JButton("Split");

        hitHand1Button = new JButton("Hit Hand 1");
        hitHand2Button = new JButton("Hit Hand 2");
        doubleDownHand1Button = new JButton("Double Down Hand 1");
        doubleDownHand2Button = new JButton("Double Down Hand 2");

        setupButtonActions();

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleDownButton);
        buttonPanel.add(splitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        checkInitialBlackjack();
    }

    /**
     * Checks if there is an initial blackjack for the player or dealer.
     */
    private void checkInitialBlackjack() {
        int playerValue = getHandValue(playerHand);
        int dealerValue = getHandValue(dealerHand);

        if (playerValue == 21) {
            JOptionPane.showMessageDialog(this, "Blackjack! You win!");
            resetGame();
        } else if (dealerValue == 21) {
            JOptionPane.showMessageDialog(this, "Dealer has Blackjack! You lose.");
            resetGame();
        }
    }

    /**
     * Deals the initial two cards to both player and dealer.
     */
    private void dealInitialCards() {
        playerHand.add(deck.dealCard());
        playerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
    }

    /**
     * Executes the dealer's turn, drawing cards until the dealer's hand value is at least 17.
     */
    private void dealerTurn() {
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(deck.dealCard());
        }
    }

    /**
     * Calculates the total value of a given hand, adjusting for aces as necessary.
     *
     * @param hand the hand to calculate the value of
     * @return the total value of the hand
     */
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

    /**
     * Checks if the player has busted, ending the game if so.
     */
    private void checkPlayerBust() {
        if (getHandValue(playerHand) > 21) {
            JOptionPane.showMessageDialog(this, "You bust! Dealer wins.");
            resetGame();
        } else if (isSplit && getHandValue(splitHand) > 21) {
            JOptionPane.showMessageDialog(this, "You bust on split hand! Dealer wins.");
            resetGame();
        }
    }

    /**
     * Determines the winner of the game and displays the result.
     */
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
        if (isSplit) {
            int splitValue = getHandValue(splitHand);
            if (splitValue > 21) {
                message = "You bust on split hand! Dealer wins.";
            } else if (dealerValue > 21 || splitValue > dealerValue) {
                message = "You win on split hand!";
            } else if (splitValue == dealerValue) {
                message = "It's a tie on split hand!";
            } else {
                message = "Dealer wins on split hand!";
            }
            JOptionPane.showMessageDialog(this, message);
        }
        resetGame();
    }

    /**
     * Resets the game by shuffling the deck and dealing new initial cards.
     */
    private void resetGame() {
        deck.shuffle();
        playerHand.clear();
        dealerHand.clear();
        splitHand.clear();
        dealInitialCards();
        doubledDown = false; // Reset double down flag
        isSplit = false; // Reset split flag
        buttonPanel.removeAll();
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleDownButton);
        buttonPanel.add(splitButton);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        tablePanel.repaint();
    }

    /**
     * Sets up the action listeners for the game buttons.
     */
    private void setupButtonActions() {
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

        doubleDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!doubledDown) {
                    doubledDown = true;
                    playerHand.add(deck.dealCard());
                    tablePanel.repaint();
                    checkPlayerBust();
                    if (getHandValue(playerHand) <= 21) {
                        dealerTurn();
                        tablePanel.repaint();
                        checkWinner();
                    }
                }
            }
        });

        splitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playerHand.size() == 2 && playerHand.get(0).getValue() == playerHand.get(1).getValue() && !isSplit) {
                    isSplit = true;
                    splitHand.add(playerHand.remove(1));
                    playerHand.add(deck.dealCard());
                    splitHand.add(deck.dealCard());
                    tablePanel.repaint();
                    updateButtonsForSplit();
                }
            }
        });

        hitHand1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerHand.add(deck.dealCard());
                tablePanel.repaint();
                checkPlayerBust();
            }
        });

        doubleDownHand1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!doubledDown) {
                    doubledDown = true;
                    playerHand.add(deck.dealCard());
                    tablePanel.repaint();
                    checkPlayerBust();
                    if (getHandValue(playerHand) <= 21) {
                        dealerTurn();
                        tablePanel.repaint();
                        checkWinner();
                    }
                }
            }
        });

        hitHand2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                splitHand.add(deck.dealCard());
                tablePanel.repaint();
                checkPlayerBust();
            }
        });

        doubleDownHand2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!doubledDown) {
                    doubledDown = true;
                    splitHand.add(deck.dealCard());
                    tablePanel.repaint();
                    checkPlayerBust();
                    if (getHandValue(splitHand) <= 21) {
                        dealerTurn();
                        tablePanel.repaint();
                        checkWinner();
                    }
                }
            }
        });
    }

    /**
     * Updates the buttons for the split hand scenario.
     */
    private void updateButtonsForSplit() {
        buttonPanel.removeAll();
        buttonPanel.add(hitHand1Button);
        buttonPanel.add(hitHand2Button);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleDownHand1Button);
        buttonPanel.add(doubleDownHand2Button);
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    /**
     * Loads the card images from the resources directory.
     *
     * @return a map of card image filenames to BufferedImage objects
     */
    private Map<String, BufferedImage> loadCardImages() {
        Map<String, BufferedImage> images = new HashMap<>();
        String[] suits = {"hearts", "diamonds", "clubs", "spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "jack", "queen", "king", "ace"};
        try {
            for (String suit : suits) {
                for (String rank : ranks) {
                    String key = rank + "_of_" + suit;
                    BufferedImage img = ImageIO.read(new File("C:\\Users\\leandro.steenkamp\\eclipse-workspace\\Test\\resources\\" + key + ".png"));
                    images.put(key, img);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading card images.");
        }
        return images;
    }

    /**
     * Custom JPanel class for drawing the game table and cards.
     */
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

            // Draw player cards and sum
            g2d.setColor(Color.WHITE);
            drawHand(g2d, playerHand, 50, 370, "Player's Hand: " + calculateHandSum(playerHand));
            if (isSplit) {
                drawHand(g2d, splitHand, 50, 480, "Split Hand: " + calculateHandSum(splitHand));
            }

            // Draw dealer cards and sum
            g2d.drawString("Dealer's Hand: " + calculateHandSum(dealerHand), 50, 50);
            drawHand(g2d, dealerHand, 50, 70, "");
        }

        private int calculateHandSum(List<Card> hand) {
            int sum = 0;
            int aceCount = 0;
            for (Card card : hand) {
                sum += card.getValue();
                if (card.getRank() == Card.Rank.ACE) {
                    aceCount++;
                }
            }
            while (sum > 21 && aceCount > 0) {
                sum -= 10;
                aceCount--;
            }
            return sum;
        }

        private void drawHand(Graphics2D g2d, List<Card> hand, int x, int y, String handName) {
            int cardWidth = 60;
            int cardHeight = 90;
            int spacing = 20;

            // Draw hand name
            if (!handName.isEmpty()) {
                g2d.drawString(handName, x, y - 20);
            }

            // Draw cards
            for (Card card : hand) {
                String key = card.getRank().toString().toLowerCase() + "_of_" + card.getSuit().toString().toLowerCase();
                BufferedImage img = cardImages.get(key);
                if (img != null) {
                    g2d.drawImage(img, x, y, cardWidth, cardHeight, null);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillRoundRect(x, y, cardWidth, cardHeight, 10, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRoundRect(x, y, cardWidth, cardHeight, 10, 10);
                    g2d.drawString(card.toString(), x + 10, y + 50);
                }
                x += cardWidth + spacing;
            }
        }
    }

    /**
     * The main method to launch the Blackjack game.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BlackjackTable table = new BlackjackTable();
            table.setVisible(true);
        });
    }
}
