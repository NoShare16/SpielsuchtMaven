package Blackjack;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



class BlackjackTable extends JFrame {
    private final Deck deck;
    private final List<Card> playerHand;
    public List<Card> getPlayerHand() {
    	return playerHand;
    }
    private final List<Card> dealerHand;
    public List<Card> getDealerHand() {
    	return playerHand;
    }
    private final TablePanel tablePanel;
    private boolean doubledDown = false; // Track if double down was used

    private final List<Card> splitHand;
    public List<Card> getSplitHand() {
    	return splitHand;
    }
   
  
    	public boolean isSplit() {
    		return isSplit();
    	}
    
    
    

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
        boolean isSplit = false;
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
                   
                    tablePanel.repaint();
                }
            });

            splitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean canSplit = playerHand.size() == 2 && playerHand.get(0).getValue() == playerHand.get(1).getValue();

                    if (canSplit) {
                        splitHand.add(playerHand.remove(1));
                        boolean isSplit = true;

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
                    tablePanel.repaint();
                }
            });

            hitHand2Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    splitHand.add(deck.dealCard());
                    checkSplitHand();
                    tablePanel.repaint();
                }
            });

            standHand1Button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dealerTurn();
                    checkWinCondition(false); // Check only for Hand 1
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

        if (isSplit()) {
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

    Map<String, BufferedImage> loadCardImages() {
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
}