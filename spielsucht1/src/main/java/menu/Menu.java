package menu;

import javax.swing.*;

import roulette.Roulette;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Menu extends JFrame {

    public Menu() {
        setTitle("Casino");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set layout manager
        setLayout(new BorderLayout());

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Load images
        ImageIcon rouletteIcon = new ImageIcon("Bilder/games/Roulette.png");
        ImageIcon blackjackIcon = new ImageIcon("Bilder/games/Blackjack Table.G03.watermarked.2k.png");

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
            	Roulette roulette = new Roulette();
                roulette.setVisible(true);
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
                new Menu().setVisible(true);
            }
        });
    }
}

class BlackjackTable extends JFrame {
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
    private JButton standHand1Button;
    private JButton standHand2Button;
    private JButton doubleDownHand1Button;
    private JButton doubleDownHand2Button;

    // Betting related components
    private JTextField betField;
    private JLabel balanceLabel;
    private int balance = 1000; // Initial balance
    private int currentBet = 0;

    public BlackjackTable() {
        setTitle("Blackjack Table");
        setSize(1640, 924);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        splitHand = new ArrayList<>();

        // Initialize the card images map
        cardImages = loadCardImages();

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
        standHand1Button = new JButton("Stand Hand 1");
        standHand2Button = new JButton("Stand Hand 2");
        doubleDownHand1Button = new JButton("Double Down Hand 1");
        doubleDownHand2Button = new JButton("Double Down Hand 2");

        setupButtonActions();

        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleDownButton);
        buttonPanel.add(splitButton);

        // Betting components
        JPanel bettingPanel = new JPanel();
        betField = new JTextField(10);
        JButton betButton = new JButton("Place Bet");
        balanceLabel = new JLabel("Balance: $" + balance);
        bettingPanel.add(new JLabel("Bet Amount:"));
        bettingPanel.add(betField);
        bettingPanel.add(betButton);
        bettingPanel.add(balanceLabel);

        betButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeBet();
            }
        });

        add(bettingPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Disable buttons until a bet is placed
        enableGameButtons(false);
        disableButtonsOnLoad(true);
    }

    private void placeBet() {
        try {
            int bet = Integer.parseInt(betField.getText());
            if (bet > balance) {
                JOptionPane.showMessageDialog(this, "Bet amount exceeds balance!");
            } else {
                currentBet = bet;
                balance -= bet;
                updateBalanceLabel();
                resetGame();
                resetButtons(); // Zurücksetzen der Buttons nach Platzieren eines neuen Einsatzes

                // Entferne alle Buttons aus dem buttonPanel
                buttonPanel.removeAll();

                // Füge die relevanten Buttons hinzu (diese können sich je nach Spielstatus ändern)
                buttonPanel.add(hitButton);
                buttonPanel.add(standButton);
                buttonPanel.add(doubleDownButton);
                buttonPanel.add(splitButton);

                // Revalidate und repaint das buttonPanel
                buttonPanel.revalidate();
                buttonPanel.repaint();

                enableGameButtons(true);
             }
         } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Invalid bet amount!");
         }
         checkInitialBlackjack(); // Überprüfen Sie sofort nach dem Austeilen der Karten
         if (getHandValue(dealerHand)==21) {
        	 hitButton.setEnabled(false);
        	 standButton.setEnabled(false);
        	 doubleDownButton.setEnabled(false);
        	 splitButton.setEnabled(false);
         }
     }
     
    private void resetButtons() {
        hitButton.setEnabled(true);
        standButton.setEnabled(true);
        doubleDownButton.setEnabled(true);
        splitButton.setEnabled(true);

        // Deaktiviere die Schaltflächen für Hand 1 und Hand 2
        hitHand1Button.setEnabled(false);
        standHand1Button.setEnabled(false);
        doubleDownHand1Button.setEnabled(false);
        hitHand2Button.setEnabled(false);
        standHand2Button.setEnabled(false);
        doubleDownHand2Button.setEnabled(false);

        // Entferne spezielle Hand-Buttons und füge die Standard-Buttons hinzu
        buttonPanel.removeAll();
        buttonPanel.add(hitButton);
        buttonPanel.add(standButton);
        buttonPanel.add(doubleDownButton);
        buttonPanel.add(splitButton);

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    public void enableGameButtons(boolean enable) {
    	if (getHandValue(playerHand) >=21) {
        hitButton.setEnabled(enable);
        standButton.setEnabled(enable);
        doubleDownButton.setEnabled(enable);
        splitButton.setEnabled(enable);
        hitHand1Button.setEnabled(enable);
        standHand1Button.setEnabled(enable);
        doubleDownHand1Button.setEnabled(enable);
        hitHand2Button.setEnabled(enable);
        standHand2Button.setEnabled(enable);
        doubleDownHand2Button.setEnabled(enable);
    	} 
    }
    
    public void disableButtonsOnLoad(boolean enable) {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleDownButton.setEnabled(false);
        splitButton.setEnabled(false);
        hitHand1Button.setEnabled(false);
        standHand1Button.setEnabled(false);
        doubleDownHand1Button.setEnabled(false);
        hitHand2Button.setEnabled(false);
        standHand2Button.setEnabled(false);
        doubleDownHand2Button.setEnabled(false);
    }
    
    public void disableButtonsForDoubleDown(boolean enable) {
    	if (getHandValue(playerHand) < 21) {
        hitButton.setEnabled(enable);
        standButton.setEnabled(enable);
        doubleDownButton.setEnabled(enable);
        splitButton.setEnabled(enable);
        hitHand1Button.setEnabled(enable);
        standHand1Button.setEnabled(enable);
        doubleDownHand1Button.setEnabled(enable);
        hitHand2Button.setEnabled(enable);
        standHand2Button.setEnabled(enable);
        doubleDownHand2Button.setEnabled(enable);
    	}
    } 

    private void updateBalanceLabel() {
        balanceLabel.setText("Balance: $" + balance);
    }

    private void checkInitialBlackjack() {
        int playerValue = getHandValue(playerHand);
        int dealerValue = getHandValue(dealerHand);

        if (getHandValue(playerHand) == 21) {
            int winnings = (int) (currentBet * 1.5);
            balance += currentBet + winnings;
            updateBalanceLabel();
            JOptionPane.showMessageDialog(this, "Blackjack! You win $" + winnings + "!");
            enableGameButtons(false);
        } else if (getHandValue(dealerHand) == 21) {
            JOptionPane.showMessageDialog(this, "Dealer has Blackjack. You lose.");
            enableGameButtons(false);            
        }
    }

    private void resetGame() {
        // Clear hands and deal new cards
        playerHand.clear();
        dealerHand.clear();
        splitHand.clear();
        isSplit = false;
        doubledDown = false;
        enableGameButtons(false);

        // Shuffle the deck
        deck.shuffle();

        // Deal initial cards
        dealInitialCards();
        

        // Check for initial blackjack
        checkInitialBlackjack();
       

        // Update the table
        tablePanel.repaint();
    }

    private void dealInitialCards() {
        playerHand.add(deck.dealCard());
        playerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
    }
    
        
        private void setupButtonActions() {
            hitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playerHand.add(deck.dealCard());
                    checkPlayerHand();
                    enableGameButtons(false);
                    tablePanel.repaint();
                }
            });

            standButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dealerTurn();
                    if (getHandValue(playerHand) < 21) {
                    	disablePlayerButtons();
                    	checkWinCondition();
                    	updateBalanceLabel();
                    }
                    //checkWinCondition();
                    tablePanel.repaint();
                }
            });

            doubleDownButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    balance -= currentBet;
                    currentBet *= 2;
                    updateBalanceLabel();
                    playerHand.add(deck.dealCard());
                    doubledDown = true;
                    checkPlayerHand();
                    dealerTurn();
                    disableButtonsForDoubleDown(false);  	
                    checkWinCondition();
                    updateBalanceLabel();
                   
                    tablePanel.repaint();
                }
            });

            splitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean canSplit = playerHand.size() == 2 && playerHand.get(0).getValue() == playerHand.get(1).getValue();

                    if (canSplit) {
                        splitHand.add(playerHand.remove(1));
                        isSplit = true;

                        balance -= currentBet;
                        updateBalanceLabel();

                        buttonPanel.removeAll();

                        // Add buttons for Hand 1
                        buttonPanel.add(hitHand1Button);
                        buttonPanel.add(standHand1Button);
                        buttonPanel.add(doubleDownHand1Button);
                        enableButtonsForSplit(true);

                        // Add buttons for Hand 2
                        buttonPanel.add(hitHand2Button);
                        buttonPanel.add(standHand2Button);
                        buttonPanel.add(doubleDownHand2Button);
                        enableButtonsForSplit(true);

                        buttonPanel.revalidate();
                        buttonPanel.repaint();
                        tablePanel.repaint();

                        // Check initial blackjack for both hands after split
                        checkPlayerHand();
                        checkSplitHand();
                    }
                }
            });

            hitHand1Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    playerHand.add(deck.dealCard());
                    checkPlayerHand();
                    updateBalanceLabel();
                    tablePanel.repaint();
                }
            });

            hitHand2Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    splitHand.add(deck.dealCard());
                    checkSplitHand();
                    updateBalanceLabel();
                    tablePanel.repaint();
                }
            });

            standHand1Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dealerTurn();
                    checkWinCondition(false); // Check only for Hand 1
                    updateBalanceLabel();
                    hitHand1Button.setEnabled(false);
                    standHand1Button.setEnabled(false);
                    doubleDownHand1Button.setEnabled(false);
                    tablePanel.repaint();
                }
            });

            standHand2Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dealerTurn();
                    checkWinCondition(true); // Check only for Hand 2
                    updateBalanceLabel();
                    hitHand2Button.setEnabled(false);
                    standHand2Button.setEnabled(false);
                    doubleDownHand2Button.setEnabled(false);
                    tablePanel.repaint();
                }
            });

            doubleDownHand1Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    balance -= currentBet;
                    currentBet *= 2;
                    updateBalanceLabel();
                    playerHand.add(deck.dealCard());
                    doubledDown = true;
                    checkPlayerHand();
                    dealerTurn();
                    enableButtonsDoubleDownHand1(false);
                    checkWinCondition(false); // Check only for Hand 1
                    updateBalanceLabel();
                    tablePanel.repaint();
                }
            });

            doubleDownHand2Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    balance -= currentBet;
                    currentBet *= 2;
                    updateBalanceLabel();
                    splitHand.add(deck.dealCard());
                    doubledDown = true;
                    enableButtonsDoubleDownHand2(false);
                    checkSplitHand();
                    checkWinCondition();
                    updateBalanceLabel();
                    tablePanel.repaint();
                }
            });
        }
        
        
private void checkWinCondition(boolean isHand2) {
    int playerValue = isHand2 ? getHandValue(splitHand) : getHandValue(playerHand);
    int dealerValue = getHandValue(dealerHand);

    StringBuilder result = new StringBuilder();

    if (playerValue > 21) {
    	
        result.append(isHand2 ? "Player's Hand 2 busts. " : "Player's Hand 1 busts. ");
        result.append("Dealer wins.");
    } else if (dealerValue > 21 || playerValue > dealerValue) {
        result.append(isHand2 ? "Player's Hand 2 wins!" : "Player's Hand 1 wins!");
        balance += currentBet * 2;
    } else if (playerValue == dealerValue) {
        result.append("Push. It's a tie.");
        balance += currentBet;
    } else {
        result.append("Dealer wins.");
    }
    updateBalanceLabel();
    JOptionPane.showMessageDialog(this, result.toString());
    enableGameButtons(false);
}
    
private void checkPlayerHand() {
    int handValue = getHandValue(playerHand);

    if (handValue == 21) {
        JOptionPane.showMessageDialog(this, "Player's Hand 1 has Blackjack!");
        disablePlayerButtons(true); // Disable buttons for Hand 1
    } else if (handValue > 21) {
        JOptionPane.showMessageDialog(this, "Player's Hand 1 busts!");
        disablePlayerButtons(true); // Disable buttons for Hand 1
    }
}

    public void disablePlayerButtons() {
        hitButton.setEnabled(false);
        standButton.setEnabled(false);
        doubleDownButton.setEnabled(false);
        splitButton.setEnabled(false);
        hitHand1Button.setEnabled(false);
        standHand1Button.setEnabled(false);
        doubleDownHand1Button.setEnabled(false);
        hitHand2Button.setEnabled(false);
        standHand2Button.setEnabled(false);
        doubleDownHand2Button.setEnabled(false);
    }
    
    public void enableButtonsForSplit(boolean enable) {
        hitHand1Button.setEnabled(enable);
        standHand1Button.setEnabled(enable);
        doubleDownHand1Button.setEnabled(enable);
        hitHand2Button.setEnabled(enable);
        standHand2Button.setEnabled(enable);
        doubleDownHand2Button.setEnabled(enable);
    }
    
    public void enableButtonsDoubleDownHand1(boolean enable) {
        hitHand1Button.setEnabled(enable);
        standHand1Button.setEnabled(enable);
        doubleDownHand1Button.setEnabled(enable);
    }
    public void enableButtonsDoubleDownHand2(boolean enable) {
        hitHand2Button.setEnabled(enable);
        standHand2Button.setEnabled(enable);
        doubleDownHand2Button.setEnabled(enable);
    }
    private void checkSplitHand() {
        int handValue = getHandValue(splitHand);

        if (handValue == 21) {
            JOptionPane.showMessageDialog(this, "Player's Hand 2 has Blackjack!");
            disablePlayerButtons(false); // Disable buttons for Hand 2
        } else if (handValue > 21) {
            JOptionPane.showMessageDialog(this, "Player's Hand 2 busts!");
            disablePlayerButtons(false); // Disable buttons for Hand 2
        }
    }

    private void dealerTurn() {
        while (getHandValue(dealerHand) < 17) {
            dealerHand.add(deck.dealCard());
        }
    }

    private void checkWinCondition() {
        int playerValue = getHandValue(playerHand);
        int dealerValue = getHandValue(dealerHand);

        StringBuilder result = new StringBuilder();

        if (playerValue > 21) {
            result.append("Player busts. Dealer wins.");
        } else if (dealerValue > 21 || playerValue > dealerValue) {
            result.append("Player wins!");
            balance += currentBet * 2;
        } else if (playerValue == dealerValue) {
            result.append("Push. It's a tie.");
            balance += currentBet;
        } else {
            result.append("Dealer wins.");
        }

        if (isSplit) {
            int splitValue = getHandValue(splitHand);

            result.append("\n");

            if (splitValue > 21) {
                result.append("Player's split hand busts. Dealer wins.");
            } else if (dealerValue > 21 || splitValue > dealerValue) {
                result.append("Player's split hand wins!");
                balance += currentBet * 2;
            } else if (splitValue == dealerValue) {
                result.append("Player's split hand: Push. It's a tie.");
                balance += currentBet;
            } else {
                result.append("Dealer wins against player's split hand.");
            }
        }

        updateBalanceLabel();
        JOptionPane.showMessageDialog(this, result.toString());
        enableGameButtons(false);
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
    
    private void disablePlayerButtons(boolean isHand1) {
        if (isHand1) {
            hitHand1Button.setEnabled(false);
            standHand1Button.setEnabled(false);
            doubleDownHand1Button.setEnabled(false);
        } else {
            hitHand2Button.setEnabled(false);
            standHand2Button.setEnabled(false);
            doubleDownHand2Button.setEnabled(false);
        }
    }

    private Map<String, BufferedImage> loadCardImages() {
        Map<String, BufferedImage> cardImages = new HashMap<>();
        String[] suits = {"clubs", "diamonds", "hearts", "spades"};
        String[] ranks = {"two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king", "ace"};

        for (String suit : suits) {
            for (String rank : ranks) {
                String cardName = rank + "_of_" + suit;
                try {
                    BufferedImage image = ImageIO.read(new File("Bilder/cards/" + cardName + ".png"));
                    cardImages.put(cardName, image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return cardImages;
    }

    class TablePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(new Color(0, 128, 0));

            drawHand(g, playerHand, 50, 450, "Player's Hand: " + calculateHandSum(playerHand));
            drawHand(g, dealerHand, 50, 50, "Dealer's Hand: " + calculateHandSum(dealerHand));
            if (isSplit) {
                drawHand(g, splitHand, 550, 450, "Split Hand: " + calculateHandSum(splitHand));
            }
        }

        private void drawHand(Graphics g, List<Card> hand, int x, int y, String label) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            if (hand == splitHand) { 
                g.drawString(label, x + 300, y - 10);
            } else {
                g.drawString(label, x, y - 10);
            }

            int cardWidth = 240;
            int cardHeight = 360;
            int overlap = 120; // Wert fÃ¼r die Ãœberlappung
            int arcWidth = 30;
			int arcHeight = 30;

            for (int i = 0; i < hand.size(); i++) {
                Card card = hand.get(i);
                String key = card.getRank().name().toLowerCase() + "_of_" + card.getSuit().name().toLowerCase();
                BufferedImage img = cardImages.get(key);

                if (img != null) {
                    g.setColor(Color.WHITE);
                    int xOffset = (hand == splitHand) ? x + 300 + i * (cardWidth - overlap) : x + i * (cardWidth - overlap);                 
					g.fillRoundRect(xOffset, y, cardWidth, cardHeight, arcWidth, arcHeight);
                    g.drawImage(img, xOffset, y, cardWidth, cardHeight, null);
                } else {
                    System.err.println("Missing image for card: " + key);
                }
            }
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
    
    }
}

class Deck {
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

class Card {
    public enum Suit {HEARTS, DIAMONDS, CLUBS, SPADES}
    public enum Rank {TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE}

    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getValue() {
        switch (rank) {
            case TWO: return 2;
            case THREE: return 3;
            case FOUR: return 4;
            case FIVE: return 5;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN:
            case JACK:
            case QUEEN:
            case KING: return 10;
            case ACE: return 11;
            default: throw new IllegalArgumentException("Unknown rank: " + rank);
        }
    }
}
