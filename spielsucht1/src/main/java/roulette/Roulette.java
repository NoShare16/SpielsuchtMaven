package roulette;

import javax.swing.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import config.Config;

import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.awt.*;
import java.awt.event.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Roulette extends JFrame {
	
	private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> players;
    
    private JLabel resultLabel;
    private JPanel roulettePanel;
    private String[] rouletteNumbers = {"0", "32", "15", "19", "4", "21", "2", "25", "17", "34", "6", "27",
                                        "13", "36", "11", "30", "8", "23", "10", "5", "24", "16", "33",
                                        "1", "20", "14", "31", "9", "22", "18", "29", "7", "28", "12", "35",
                                        "3", "26"};
    private String[] rouletteNumbersForButtons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
            "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
            "35", "36"};
    private String[] redNumbers = {"1", "3", "5", "7", "9", "12", "14", "16", "18", "19", "21", "23", 
                                   "25", "27", "30", "32", "34", "36"};
    private String[] column1_34 = {"1", "4", "7", "10", "13", "16", "19", "22", "25", "28", "31", "34"};
    private String[] column2_35 = {"2", "5", "8", "11", "14", "17", "20", "23", "26", "29", "32", "35"};
    private String[] column3_36 = {"3", "6", "9", "12", "15", "18", "21", "24", "27", "30", "33", "36"};
    
    private int angle = 0;
    private Timer timer;
    private String Eingabe;
    private Color Green = new Color(0, 102, 0);
    private Color DarkRed = new Color(153, 0, 0);
    private Border buttonBorder = new LineBorder(Color.WHITE, 1);
    private  Font font = new Font("Times New Roman", Font.BOLD, 20);

    public Roulette() {
    	
        setTitle("Animated Roulette");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initMongoDB();

        roulettePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRoulette(g);
                drawBall(g);
            }
            
            
        };
        

        roulettePanel.setPreferredSize(new Dimension(1200, 600));
        roulettePanel.setBackground(Green);
        roulettePanel.setLayout(null); // Deaktiviere das Layout-Management

        add(roulettePanel, BorderLayout.CENTER);

        JLabel EinsatzLabel = new JLabel("Please enter your bet!");
        EinsatzLabel.setBounds(650, 462, 150, 25);
        EinsatzLabel.setForeground(Color.WHITE);
        roulettePanel.add(EinsatzLabel);
        JTextField EinsatzFeld = new JTextField();
        EinsatzFeld.setBounds(800, 462, 100, 25);
        roulettePanel.add(EinsatzFeld);
        drawButtons(); // Füge die Buttons zur Roulette-Tafel hinzu

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initMongoDB() {
        mongoClient = MongoClients.create(Config.MONGO_CONNECTION_STRING); // Use Config or env variables
        database = mongoClient.getDatabase("Roulette");
        players = database.getCollection("players");
    }
    
    private void updatePlayerBalance(int playerId, double change) {
        // This is a simplified update function for player balances
        Document player = players.find(new Document("playerId", playerId)).first();
        if (player != null) {
            double currentBalance = player.getDouble("balance");
            players.updateOne(new Document("playerId", playerId), 
                              new Document("$set", new Document("balance", currentBalance + change)));
        }
    }
    
    private void logBetAndResult(int playerId, double betAmount, String betColor, int betNumber, boolean win) {
        // You could add more details as needed
        Document betLog = new Document("playerId", playerId)
                                    .append("betAmount", betAmount)
                                    .append("win", win)
                                    .append("betColor", betColor)
                                    .append("betNumber", betNumber)
                                    .append("timestamp", System.currentTimeMillis());
        database.getCollection("bet_logs").insertOne(betLog);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    private void drawButtons() {
        JButton[] rouletteButtons = new JButton[37];

        for (int i = 0; i < rouletteButtons.length; i++) {
            rouletteButtons[i] = new JButton("" + i);
            if (rouletteNumbers[i].equals("0")) {
                rouletteButtons[i].setBackground(Green);
                rouletteButtons[i].setForeground(Color.WHITE);
                rouletteButtons[i].setFont(font);
            } else if (isRed(rouletteNumbersForButtons[i]) == "Red") {
                rouletteButtons[i].setBackground(Color.RED);
                rouletteButtons[i].setForeground(Color.WHITE);
                rouletteButtons[i].setFont(font);
            } else {
                rouletteButtons[i].setBackground(Color.BLACK);
                rouletteButtons[i].setForeground(Color.WHITE);
                rouletteButtons[i].setFont(font);// Weiße Schrift für schwarze Buttons
            }
            rouletteButtons[i].setBorder(buttonBorder);
            roulettePanel.add(rouletteButtons[i]);

            if (i == 0) {
                rouletteButtons[i].setBounds(430, 90, 70, 180); // Setze Position und Größe des "0"-Buttons
            } else {
                int row = (i - 1) % 3;
                int column = (i - 1) / 3;
                int x = 500 + column * 50; // Berechne die x-Position basierend auf der Spalte
                int y = 90 + row * 60; // Berechne die y-Position basierend auf der Zeile
                rouletteButtons[i].setBounds(x, y, 50, 60); // Setze Position und Größe des Buttons
            }
            final int index = i;
            rouletteButtons[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Eingabe = rouletteButtons[index].getText();
				}
        	});
        }
        
        JButton twotoone1 = new JButton("2 to 1");
        twotoone1.setBackground(Green);
        twotoone1.setForeground(Color.WHITE);
        twotoone1.setBorder(buttonBorder);
        roulettePanel.add(twotoone1);
        twotoone1.setBounds(1100, 90, 70, 60);
        twotoone1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "1-34";
			}
    	});
        
        JButton twotoone2 = new JButton("2 to 1");
        twotoone2.setBackground(Green);
        twotoone2.setForeground(Color.WHITE);
        twotoone2.setBorder(buttonBorder);
        roulettePanel.add(twotoone2);
        twotoone2.setBounds(1100, 150, 70, 60);
        twotoone2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "2-35";
			}
    	});
        
        JButton twotoone3 = new JButton("2 to 1");
        twotoone3.setBackground(Green);
        twotoone3.setForeground(Color.WHITE);
        twotoone3.setBorder(buttonBorder);
        roulettePanel.add(twotoone3);
        twotoone3.setBounds(1100, 210, 70, 60);
        twotoone3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "3-36";
			}
    	});
        
        JButton onest12 = new JButton("1st 12");
        onest12.setBackground(Green);
        onest12.setForeground(Color.WHITE);
        onest12.setBorder(buttonBorder);
        roulettePanel.add(onest12);
        onest12.setBounds(500, 270, 200, 60);
        onest12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "1st 12";
			}
    	});
        
        JButton twond12 = new JButton("2nd 12");
        twond12.setBackground(Green);
        twond12.setForeground(Color.WHITE);
        twond12.setBorder(buttonBorder);
        roulettePanel.add(twond12);
        twond12.setBounds(700, 270, 200, 60);
        twond12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "2nd 12";
			}
    	});
        
        JButton threerd12 = new JButton("3rd 12");
        threerd12.setBackground(Green);
        threerd12.setForeground(Color.WHITE);
        threerd12.setBorder(buttonBorder);
        roulettePanel.add(threerd12);
        threerd12.setBounds(900, 270, 200, 60);
        threerd12.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "3rd 12";
			}
    	});
        
        JButton one18 = new JButton("1 to 18");
        one18.setBackground(Green);
        one18.setForeground(Color.WHITE);
        one18.setBorder(buttonBorder);
        roulettePanel.add(one18);
        one18.setBounds(500, 330, 100, 60);
        one18.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "1 to 18";
			}
    	});
        
        JButton even = new JButton("Even");
        even.setBackground(Green);
        even.setForeground(Color.WHITE);
        even.setBorder(buttonBorder);
        roulettePanel.add(even);
        even.setBounds(600, 330, 100, 60);
        even.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "Even";
			}
    	});
        
        JButton Red = new JButton();
        Red.setBackground(Color.RED);
        Red.setBorder(buttonBorder);
        roulettePanel.add(Red);
        Red.setBounds(700, 330, 100, 60);
        Red.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "Red";
			}
    	});
        
        JButton Black = new JButton();
        Black.setBackground(Color.BLACK);
        Black.setBorder(buttonBorder);
        roulettePanel.add(Black);
        Black.setBounds(800, 330, 100, 60);
        Black.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "Black";
			}
    	});
        
        JButton odd = new JButton("Odd");
        odd.setBackground(Green);
        odd.setForeground(Color.WHITE);
        odd.setBorder(buttonBorder);
        roulettePanel.add(odd);
        odd.setBounds(900, 330, 100, 60);
        odd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "Odd";
			}
    	});
        
        JButton nineteento36 = new JButton("19 to 36");
        nineteento36.setBackground(Green);
        nineteento36.setForeground(Color.WHITE);
        nineteento36.setBorder(buttonBorder);
        roulettePanel.add(nineteento36);
        nineteento36.setBounds(1000, 330, 100, 60);
        nineteento36.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Eingabe = "19 to 36";
			}
    	});
        
        JButton readyButton = new JButton("Ready!");
        readyButton.setBackground(DarkRed);
        readyButton.setForeground(Color.WHITE);
        readyButton.setBorder(buttonBorder);
        roulettePanel.add(readyButton);
        readyButton.setBounds(930, 450, 100, 50);

        readyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                spinRoulette();
            }
        });
        
        
    }

    private String isRed(String number) {
        for (String redNumber : redNumbers) {
            if (number.equals(redNumber))
                return "Red";
        }
        return "Black";
    }
    
    private String isEven(String input) {
    	int number = Integer.parseInt(input);
    	double result = number % 2;
    	if (result == 0) {
    		return "Even";
    	}
    	return "Odd";
    }
    
    private String twotoone (String Input) {
    	for (String columnNumber : column1_34) {
            if (Input.equals(columnNumber))
                return "1-34";
        }
    	for (String columnNumber : column2_35) {
            if (Input.equals(columnNumber))
                return "2-35";
        }
    	for (String columnNumber : column3_36) {
            if (Input.equals(columnNumber))
                return "3-36";
        }
    	return "";
    }
    
    private String isBetween (int input) {
    	if (1 <= input && input <= 12) {
    		return "1st 12";
    	} else if (13 <= input && input <= 24) {
    		return "2nd 12";
    	} else if (25 <= input && input <= 36) {
    		return "3rd 12";
    	} else if (1 <= input && input <= 18) {
    		return "1 to 18";
    	} else if (19 <= input && input <= 36) {
    		return "19 to 36";
    	}
    	return "";
    }
    
    private void drawRoulette(Graphics g) {
        int radius = 170;
        int centerX = roulettePanel.getWidth() / 6;
        int centerY = roulettePanel.getHeight() / 2;

        g.setColor(Color.WHITE);
        g.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

        for (int i = 0; i < rouletteNumbers.length; i++) {
            double angleRadians = Math.toRadians(i * (360.0 / rouletteNumbers.length));
            int numberX = (int) (centerX + Math.cos(angleRadians) * (radius - 30) - 5);
            int numberY = (int) (centerY + Math.sin(angleRadians) * (radius - 30) + 5);
            if (isRed(rouletteNumbers[i]) == "Red")
                g.setColor(Color.RED);
            else if (rouletteNumbers[i].equals("0"))
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);
            g.drawString(rouletteNumbers[i], numberX, numberY);
        }
    }

    private void drawBall(Graphics g) {
        int radius = 10;
        int centerX = roulettePanel.getWidth() / 6;
        int centerY = roulettePanel.getHeight() / 2;

        double angleRadians = Math.toRadians(angle);
        int ballX = (int) (centerX + Math.cos(angleRadians) * (170 - radius));
        int ballY = (int) (centerY + Math.sin(angleRadians) * (170 - radius));

        g.setColor(Color.YELLOW);
        g.fillOval(ballX - radius, ballY - radius, radius * 2, radius * 2);
    }

    private void spinRoulette() {
        angle = 0;
        System.out.println(Eingabe);
        int position = (int) (Math.random() * rouletteNumbers.length);
        timer = new Timer(15, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                angle += 5;
                if (angle >= position * (360.0 / rouletteNumbers.length) + 720) {
                    timer.stop();
                    showResult(position);
                }
                roulettePanel.repaint();
            }
        });
        timer.start();
    }

    private void showResult(int resultIndex) {
        if(Eingabe == rouletteNumbers[resultIndex]) {
        	// Wird die Bedingung erfüllt
        } else if(Eingabe == isRed(rouletteNumbers[resultIndex])) {
        	// Wird die Bedingung erfüllt
        	System.out.println(isRed(rouletteNumbers[resultIndex]));
        } else if (Eingabe == isEven(rouletteNumbers[resultIndex])) {
        	// Wird die Bedingung erfüllt
        	System.out.println(isEven(rouletteNumbers[resultIndex]));
        } else if (Eingabe == twotoone(rouletteNumbers[resultIndex])) {
        	// Wird die Bedingung erfüllt
        	System.out.println(rouletteNumbers[resultIndex]);
        } else if (Eingabe == isBetween(Integer.parseInt(rouletteNumbers[resultIndex]))) {
        	// Wird die Bedingung erfüllt
        	System.out.println(isBetween(Integer.parseInt(rouletteNumbers[resultIndex])));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Roulette();
            }
        });
    }
}
