package Blackjack;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Blackjack.BlackjackTable;
import Blackjack.Card;




class TablePanel extends JPanel {
	BlackjackTable blackjacktable = new BlackjackTable(); 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0, 128, 0));

        drawHand(g, blackjacktable.getPlayerHand(), 50, 450, "Player's Hand: " + calculateHandSum(blackjacktable.getPlayerHand()));
        drawHand(g, blackjacktable.getDealerHand(), 50, 50, "Dealer's Hand: " + calculateHandSum(blackjacktable.getDealerHand()));
        if (blackjacktable.isSplit()) {
            drawHand(g, blackjacktable.getSplitHand(), 550, 450, "Split Hand: " + calculateHandSum(blackjacktable.getSplitHand()));
        }
    }

    private void drawHand(Graphics g, List<Card> hand, int x, int y, String label) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        if (hand == blackjacktable.getSplitHand()) { 
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
            BufferedImage img = (BufferedImage) blackjacktable.loadCardImages().get(key);
            
        

            if (img != null) {
                g.setColor(Color.WHITE);
                int xOffset = (hand == blackjacktable.getSplitHand()) ? x + 300 + i * (cardWidth - overlap) : x + i * (cardWidth - overlap);                 
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