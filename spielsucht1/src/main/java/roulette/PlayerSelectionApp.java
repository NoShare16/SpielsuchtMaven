package roulette;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bson.types.ObjectId;

public class PlayerSelectionApp extends JFrame {
    private JComboBox<String> playerComboBox;
    private static final ObjectId[] PLAYER_IDS = {
        new ObjectId("66560a546ab1d7f2d5fbc326"),
        new ObjectId("66560a686ab1d7f2d5fbc327"),
        new ObjectId("66560a6c6ab1d7f2d5fbc328"),
        new ObjectId("66560a6e6ab1d7f2d5fbc329")
    };
    private int selectedPlayer;  // 0 means no player selected

    public PlayerSelectionApp() {
        setTitle("Select a Player");
        setSize(300, 100);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String[] playerOptions = {"Player 1", "Player 2", "Player 3", "Player 4"};
        playerComboBox = new JComboBox<>(playerOptions);
        add(playerComboBox);

        JButton selectButton = new JButton("Select Player");
        add(selectButton);

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedPlayer(playerComboBox.getSelectedIndex() + 1);
                JOptionPane.showMessageDialog(null, "Player " + selectedPlayer + " selected. ID: " + PLAYER_IDS[selectedPlayer - 1].toHexString());
            }
        });

        setVisible(true);
    }

    private void setSelectedPlayer(int playerNumber) {
        if (playerNumber < 1 || playerNumber > 4) {
            JOptionPane.showMessageDialog(this, "Invalid player number: " + playerNumber);
            return;
        }
        this.selectedPlayer = playerNumber;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PlayerSelectionApp();
            }
        });
    }
}

