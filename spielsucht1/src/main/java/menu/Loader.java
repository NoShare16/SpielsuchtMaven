package menu;


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

public class Loader extends JFrame {

    public Loader() {
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
                new Loader().setVisible(true);
            }
        });
    }
}
