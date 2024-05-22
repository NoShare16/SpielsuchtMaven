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