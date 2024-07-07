

	package roulette_v1;

	import javax.swing.*;

	import com.mongodb.client.model.UpdateOptions;
	import org.bson.types.ObjectId;

	import database.GamePollingService;
	import database.GameSession;

	import com.mongodb.client.FindIterable;
	import com.mongodb.client.MongoClient;
	import com.mongodb.client.MongoClients;
	import com.mongodb.client.MongoDatabase;
	import config.Config;
	import com.mongodb.client.MongoCollection;
	import org.bson.Document;
	import static com.mongodb.client.model.Filters.eq;
	import static com.mongodb.client.model.Filters.in;


	import java.awt.*;
	import java.awt.event.*;
	import javax.swing.border.Border;
	import javax.swing.border.LineBorder;

	import java.util.Arrays;
	import java.util.concurrent.Executors;
	import java.util.concurrent.ScheduledExecutorService;
	import java.util.concurrent.TimeUnit;

	import database.GamePollingService;


	public class Roulette extends JFrame {
		
		 private GamePollingService gamePollingService;

		private MongoClient mongoClient;
	    private MongoDatabase database;
	    
	    private ObjectId resultsId = new ObjectId("6659d0462e715afd57fd337b");
	    
	    private MongoCollection<Document> players;
	    private static final ObjectId PLAYER_1_ID = new ObjectId("66560a546ab1d7f2d5fbc326");
	    private static final ObjectId PLAYER_2_ID = new ObjectId("66560a686ab1d7f2d5fbc327");
	    private static final ObjectId PLAYER_3_ID = new ObjectId("66560a6c6ab1d7f2d5fbc328");
	    private static final ObjectId PLAYER_4_ID = new ObjectId("66560a6e6ab1d7f2d5fbc329");
	    private ObjectId selectedPlayer;
	    
	    
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
	    ObjectId[] playerIds = {PLAYER_1_ID, PLAYER_2_ID, PLAYER_3_ID, PLAYER_4_ID};
	    double[] balances = new double[4];
	    Timer ausgabe;
	    public Roulette() {
	    	setTitle("Animated Roulette");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        initMongoDB();
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
	        
	        drawButtons();

	        startButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                switch (playerComboBox.getSelectedIndex()) {
	                    case 0:
	                        selectedPlayer = PLAYER_1_ID;
	                        break;
	                    case 1:
	                        selectedPlayer = PLAYER_2_ID;
	                        break;
	                    case 2:
	                        selectedPlayer = PLAYER_3_ID;
	                        break;
	                    case 3:
	                        selectedPlayer = PLAYER_4_ID;
	                        break;
	                }
	                settingAllfalse();
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
	                GamePollingService service = new GamePollingService();
	                startReadyCheckPolling();
	            }
	        });

	        pack();
	        setLocationRelativeTo(null);
	        setVisible(true);
	    }
	    
	    public boolean soloMode() {
	    	if (gameComboBox.getSelectedIndex() == 1)
	    		return false;
	    	else {
	    		return true;
	    	}
	    }
	    	
	    
	    
	    
	    public void settingAllfalse() {
	    	if(selectedPlayer == PLAYER_1_ID) {
	    		updatePlayerData(PLAYER_1_ID, 1000, false);
	            updatePlayerData(PLAYER_2_ID, 1000, false);
	            updatePlayerData(PLAYER_3_ID, 1000, false);
	            updatePlayerData(PLAYER_4_ID, 1000, false);
	    	}
	    	if (gameComboBox.getSelectedIndex() == 0)
	    		balance = 1000;
	    }
	   
	  
	    
	    private void initMongoDB() {
	        mongoClient = MongoClients.create(Config.MONGO_CONNECTION_STRING); 
	        database = mongoClient.getDatabase("Roulette");
	        players = database.getCollection("players");
	    }
	    
	    private void updatePlayerData(ObjectId playerId, double change, boolean readyState) {
	        Document player = players.find(new Document("_id", playerId)).first();
	        if (player != null) {
	            double currentBalance = player.getDouble("balance");
	            
	            Document update = new Document("$set", new Document("balance", change)
	                                                         .append("readyState", readyState));
	            players.updateOne(new Document("_id", playerId), update);
	            System.out.println("Updated balance and ready state for player ID " + playerId.toHexString());
	        } else {
	            System.out.println("No player found with ID " + playerId.toHexString());
	        }
	    }

	    private boolean checkAllPlayersReady() {
	        ObjectId[] playerIds = {PLAYER_1_ID, PLAYER_2_ID, PLAYER_3_ID, PLAYER_4_ID};
	        MongoCollection<Document> players = database.getCollection("players");
	        for (ObjectId playerId : playerIds) {
	            Document player = players.find(eq("_id", playerId)).first();
	            if (player == null || !player.getBoolean("readyState", false)) {
	                return false; 
	            }
	        }
	        return true; 
	    }
	    
	    private boolean checkPlayerFetching() {
	    	ObjectId[] playerIds = {PLAYER_1_ID, PLAYER_2_ID, PLAYER_3_ID, PLAYER_4_ID};
	    	MongoCollection<Document> players = database.getCollection("players");
	    	for (ObjectId playerId : playerIds) {
	            Document player = players.find(eq("_id", playerId)).first();
	            if (player == null || !player.getBoolean("readyForFetching", false)) {
	                return false; 
	            }
	        }
	        return true; 
	    }
	    
	    public void updateAllBalances() {
	    	if(!soloMode()) {
		        MongoCollection<Document> players = database.getCollection("players");
		        
		
		        for (int i = 0; i < playerIds.length; i++) {
		            Document playerDocument = players.find(eq("_id", playerIds[i])).first();
		            if (playerDocument != null) {
		                Double fetchedBalance = playerDocument.getDouble("balance");
		                if (fetchedBalance != null) {
		                    balances[i] = fetchedBalance;
		                    drawPlayerInfo();
		                    balance = balances[playerComboBox.getSelectedIndex()];
		                    System.out.println("Fetched balance for player " + (i + 1) + ": " + balances[i]);
		                } else {
		                    System.out.println("Balance not set for player ID: " + playerIds[i].toHexString());
		                }
		            } else {
		                System.out.println("No player found with ID: " + playerIds[i].toHexString());
		            }
		        }
	    	}
	    	
		
		        
		        
		    }
	    
	    public void drawPlayerInfo() {
	    	if (gameComboBox.getSelectedIndex() == 1) {
	    		for (int i = 0; i < playerIds.length; i++) {
	    			showPlayerInfo[i].setText("<html>" + playerOptions[i] + "<p/>Balance: " + Double.toString(balances[i]) + "</html>");
	    		}
	    	} else
	    		showPlayerInfo[0].setText("Balance: " + Double.toString(balance));
	    }


	    
	    public void startReadyCheckPolling() {
	    	if(!soloMode()) {
	    		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	            Runnable checkReadyStatusTask = () -> {
	            	
	                System.out.println("Checking if all players are ready...");
	                boolean allReady = checkAllPlayersReady();            
	                
	                
	                if (allReady) {
	                    System.out.println("All players are ready. Starting game...");
	                    randomRoll();
	                    
	                } else {
	                    System.out.println("Not all players are ready.");
	                }
	                
	                fetchResults();
	                updateAllBalances();
	                
	            };
	            scheduler.scheduleAtFixedRate(checkReadyStatusTask, 0, 3, TimeUnit.SECONDS);
	    	}
	        
	    }
	    
	    public int fetchResults() {
	        MongoCollection<Document> players = database.getCollection("players");
	        MongoCollection<Document> results = database.getCollection("results");

	        // Check if the selected player is ready for fetching
	        Document selectedPlayerDoc = players.find(eq("_id", selectedPlayer)).first();
	        if (selectedPlayerDoc != null && selectedPlayerDoc.getBoolean("readyForFetching", false)) {
	            // Fetch the result document
	            Document resultDocument = results.find(eq("_id", resultsId)).first();
	            if (resultDocument != null) {
	                int result = resultDocument.getInteger("resultNumber", -1);
	                if (result != -1) {
	                    // Process the result
	                    calculateBalance(result);
	                    spinRoulette(result);

	                    // Update the 'readyForFetching' flag for the selected player to false
	                    Document update = new Document("$set", new Document("readyForFetching", false));
	                    players.updateOne(eq("_id", selectedPlayer), update);
	                    System.out.println("Player " + selectedPlayer.toHexString() + " 'readyForFetching' set to false.");

	                    return result;
	                } else {
	                    System.out.println("Result number is invalid for ID: " + resultsId.toHexString());
	                    return -1; 
	                }
	            } else {
	                System.out.println("No result found for ID: " + resultsId.toHexString());
	                return -1; 
	            }
	        } else {
	            System.out.println("Selected player not ready for fetching or does not exist, Player ID: " + selectedPlayer.toHexString());
	            return -1; 
	        }
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
	                bet = Double.valueOf(EinsatzFeld.getText());
	                balance = balance - bet;
	                drawPlayerInfo();
	                if (!soloMode()) {
	                	updatePlayerData(selectedPlayer, balance, true);
	                } else {
	                	randomRoll();
	                }
	                
	                System.out.println(balance);
	                
	               
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


	        for (int i = 0; i < rouletteNumbers.length; i++) {
	            double angleRadians = Math.toRadians(i * (360.0 / rouletteNumbers.length));
	            double nextAngleRadians = Math.toRadians((i + 1) * (360.0 / rouletteNumbers.length));

	            // Berechnung der Eckpunkte der Sektoren
	            int x1 = (int) (centerX + -Math.sin(angleRadians) * radius);
	            int y1 = (int) (centerY + -Math.cos(angleRadians) * radius);
	            int x2 = (int) (centerX + -Math.sin(nextAngleRadians) * radius);
	            int y2 = (int) (centerY + -Math.cos(nextAngleRadians) * radius);

	            // Füllung der Sektoren basierend auf der Farbe
	            if ("Red".equals(isRed(rouletteNumbers[i]))) {
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
	    
	    private void randomRoll() {
	    	if (selectedPlayer == PLAYER_1_ID) {
		        final int position = (int) (Math.random() * rouletteNumbers.length);  
		        boolean resultLogged = false; 
		        if (!resultLogged) {
		            System.out.println("Random roll result: " + rouletteNumbers[position]);
		            
		            if (!soloMode()) {
		            	logGameResult(position);
		            } else {
		            	spinRoulette(position);
		            }
		            
	 
		            
		            resultLogged = true; 
		        }
	    	} else {
	    		System.out.println("Not all players are ready.");
	    	}
	    }
	    
	    
	    
	    private void spinRoulette(int position) {
	        angle = 0;
	        timer = new Timer(15, new ActionListener() {
	            private boolean resultLogged = false;  
	            public void actionPerformed(ActionEvent e) {
	                angle += 5;
	                if (angle >= position * (360.0 / rouletteNumbers.length) + 720) {
	                    timer.stop();
	                    calculateBalance(position);
	                }
	                roulettePanel.repaint();
	            }
	        });
	        timer.start();
	        
	        
	    }


	    

	    public void logGameResult(int resultNumber) {
	        MongoCollection<Document> results = database.getCollection("results");
	        MongoCollection<Document> players = database.getCollection("players");

	        
	        Document resultsFilter = new Document("_id", resultsId);
	        Document resultsUpdate = new Document("$set", new Document("resultNumber", resultNumber));
	        results.updateOne(resultsFilter, resultsUpdate);
	        System.out.println("Result updated: Number=" + resultNumber);

	        
	        Document playersUpdate = new Document("$set", new Document("readyForFetching", true));
	        players.updateMany(new Document(), playersUpdate); 
	        System.out.println("All players set to 'readyForFetching' = true.");
	    }


	    

	    


	    private void calculateBalance(int resultIndex) {
	    	
	    	ausgabe = new Timer(2, new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
			        if(Eingabe == rouletteNumbers[resultIndex]) {
			        	JOptionPane.showMessageDialog(null,  "Sie haben gewonnen!");
			        	balance = balance + bet*35;
			        } else if(Eingabe == isRed(rouletteNumbers[resultIndex])) {
			        	JOptionPane.showMessageDialog(null,  "Sie haben gewonnen!");
			        	balance = balance + bet*2;
			        } else if (Eingabe == isEven(rouletteNumbers[resultIndex])) {
			        	JOptionPane.showMessageDialog(null,  "Sie haben gewonnen!");
			        	balance = balance + bet*2;
			        } else if (Eingabe == twotoone(rouletteNumbers[resultIndex])) {
			        	JOptionPane.showMessageDialog(null,  "Sie haben gewonnen!");
			        	balance = balance + bet*3;
			        } else if (Eingabe == isBetween(Integer.parseInt(rouletteNumbers[resultIndex]))) {
			        	JOptionPane.showMessageDialog(null,  "Sie haben gewonnen!");
			        	balance = balance + bet*3;
			        } else {
			        	JOptionPane.showMessageDialog(null, "Leider verloren!");
			        	bet = 0;
			        }
			        if(!soloMode()) {
			        	updatePlayerData(selectedPlayer,balance, false);
			        }
			        drawPlayerInfo();
			        ausgabe.stop();
	    		}
	    	});
	        ausgabe.start();
	    }
	    
	    public static void main(String[] args) {
	    	
	    	
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                new Roulette();
	                
	            }
	        });
	    }
	}

