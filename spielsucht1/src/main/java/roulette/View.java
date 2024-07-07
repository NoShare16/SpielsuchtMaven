package roulette;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class View extends JFrame {
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
    private boolean readyState = false;
    private double balance;
    private double bet;
    private JTextField EinsatzFeld = new JTextField();
    private JLabel[] showPlayerInfo = new JLabel[4];
    String[] playerOptions = {"Player 1", "Player 2", "Player 3", "Player 4"};
    JComboBox playerComboBox = new JComboBox<>(playerOptions);
    private boolean win;
    String[] gameOptions = {"Singleplayer", "Multiplayer"};
    JComboBox gameComboBox = new JComboBox<>(gameOptions);
    JButton startButton = new JButton("Start Game");
    Timer ausgabe;
    private Logic l;
	private double[] balances = new double[4];
	
	public View() {
		
    	setTitle("Roulette");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        add(gameComboBox);
        gameComboBox.addItemListener (new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (gameComboBox.getSelectedIndex() == 1){
					setLayout(new FlowLayout());
		        	add(playerComboBox);
		        	add(startButton);
		        	pack();
		        	repaint();
		        } else {
		        	remove(playerComboBox);
		        	add(startButton);
		        	pack();
		        	repaint();
		        }
			}
        });
        
        add(startButton);

        roulettePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawRoulette(g);
                drawBall(g);
                drawButtons();
            }
        };

        
        

        JLabel EinsatzLabel = new JLabel("Please enter your bet!");
        EinsatzLabel.setBounds(600, 462, 250, 25);
        EinsatzLabel.setForeground(Color.WHITE);
        EinsatzLabel.setFont(font);
        roulettePanel.add(EinsatzLabel);
        EinsatzFeld.setBounds(800, 462, 100, 25);
        roulettePanel.add(EinsatzFeld);
        for (int i = 0; i < showPlayerInfo.length; i++) {
        	showPlayerInfo[i] = new JLabel();
        	showPlayerInfo[i].setBounds(300*i+10, 10, 200, 50);
        	showPlayerInfo[i].setForeground(Color.WHITE);
        	showPlayerInfo[i].setFont(font);
            roulettePanel.add(showPlayerInfo[i]);
        }
        
        

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                l.setPlayer(gameComboBox.getSelectedIndex());
                drawPlayerInfo();
                getContentPane().removeAll();
                setLayout(new BorderLayout());
                add(roulettePanel, BorderLayout.CENTER);
                roulettePanel.setBackground(Green);
                roulettePanel.setLayout(null);
                setSize(1200, 600);
                validate();
                repaint();
                setLocationRelativeTo(null);
                
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
	
	public void setLogic (Logic logic) {
		this.l = logic;
	}
	
	public void setSoloMode() {
    	if (gameComboBox.getSelectedIndex() == 1)
    		l.soloMode(false);
    	else {
    		l.soloMode(true);
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
            } else if (l.isRed(rouletteNumbersForButtons[i]) == "Red") {
                rouletteButtons[i].setBackground(Color.RED);
                rouletteButtons[i].setForeground(Color.WHITE);
                rouletteButtons[i].setFont(font);
            } else {
                rouletteButtons[i].setBackground(Color.BLACK);
                rouletteButtons[i].setForeground(Color.WHITE);
                rouletteButtons[i].setFont(font);// 
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
        twotoone1.setFont(font);
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
        twotoone2.setFont(font);
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
        twotoone3.setFont(font);
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
        onest12.setFont(font);
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
        twond12.setFont(font);
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
        threerd12.setFont(font);
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
        one18.setFont(font);
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
        even.setFont(font);
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
        odd.setFont(font);
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
        nineteento36.setFont(font);
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
        readyButton.setFont(font);
        roulettePanel.add(readyButton);
        readyButton.setBounds(930, 450, 100, 50);
        readyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	balance = l.getBalance();
                bet = Double.valueOf(EinsatzFeld.getText());
                balance = balance - bet;
                if (gameComboBox.getSelectedIndex() == 1) {
                	l.updatePlayerData(l.getPlayer(playerComboBox.getSelectedIndex()), balance, true);
                } else {
                	l.setBalance(balance);
                	l.randomRoll();
                }
                drawPlayerInfo();
                System.out.println(balance);
                
               
            }
        });


    }
	
	private void drawRoulette(Graphics g) {
        int radius = 170;
        int centerX = roulettePanel.getWidth() / 6;
        int centerY = roulettePanel.getHeight() / 2;


        for (int i = 0; i < rouletteNumbers.length; i++) {
            double angleRadians = Math.toRadians(i * (360.0 / rouletteNumbers.length));
            double nextAngleRadians = Math.toRadians((i + 1) * (360.0 / rouletteNumbers.length));

            // Berechnung der Eckpunkte der Sektoren
            int x1 = (int) (centerX + -Math.sin(angleRadians) * radius);
            int y1 = (int) (centerY + -Math.cos(angleRadians) * radius);
            int x2 = (int) (centerX + -Math.sin(nextAngleRadians) * radius);
            int y2 = (int) (centerY + -Math.cos(nextAngleRadians) * radius);

            // Füllung der Sektoren basierend auf der Farbe
            if ("Red".equals(l.isRed(rouletteNumbers[i]))) {
                g.setColor(Color.RED);
            } else if (rouletteNumbers[i].equals("0")) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLACK);
            }
            g.fillPolygon(new int[]{centerX, x1, x2}, new int[]{centerY, y1, y2}, 3);

            // Zeichnen der Zahl
            double midAngleRadians = (angleRadians + nextAngleRadians) / 2;
            int numberX = (int) (centerX + -Math.sin(midAngleRadians) * (radius - 15) - 10);
            int numberY = (int) (centerY + -Math.cos(midAngleRadians) * (radius - 15) + 5);
            g.setColor(Color.WHITE);
            g.setFont(font);
            g.drawString(rouletteNumbers[i], numberX, numberY);
        }
        
        
    }
	
	private void drawBall(Graphics g) {
        int radius = 10;
        int centerX = roulettePanel.getWidth() / 6;
        int centerY = roulettePanel.getHeight() / 2;

        double angleRadians = Math.toRadians(angle);
        int ballX = (int) (centerX + -Math.sin(angleRadians) * (140 - radius));
        int ballY = (int) (centerY + -Math.cos(angleRadians) * (140 - radius));

        g.setColor(Color.WHITE);
        g.fillOval(ballX - radius, ballY - radius, radius * 2, radius * 2);
    }
	
	public void drawPlayerInfo() {
    	if (gameComboBox.getSelectedIndex() == 1) {
    		for (int i = 0; i < 4; i++) {
    			showPlayerInfo[i].setText("<html>" + playerOptions[i] + "<p/>Balance: " + Double.toString(balances[i]) + "</html>");
    			System.out.println(l.getPlayerBalances(i));
    		}
    	} else
    		showPlayerInfo[0].setText("Balance: " + Double.toString(l.getBalance()));
    }
	
	void spinRoulette(int position) {
        angle = 0;
        timer = new Timer(15, new ActionListener() {
            private boolean resultLogged = false;  
            public void actionPerformed(ActionEvent e) {
                angle += 5;
                if (angle >= position * (360.0 / rouletteNumbers.length) + 720) {
                    timer.stop();
                    l.calculateBalance(Eingabe, bet, position);
                }
                roulettePanel.repaint();
            }
        });
        timer.start();
        
        
    }
	
	public int getPlayerSelectedIndex() {
		return playerComboBox.getSelectedIndex();
	}
	
	public void setPlayerBalances(double[] balances) {
		for (int i = 0; i <= balances.length; i++) {
			this.balances[i] = balances[i];
		}
	}
	
}
