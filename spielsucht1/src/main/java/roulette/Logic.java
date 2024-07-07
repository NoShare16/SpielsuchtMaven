
	package roulette;

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


	public class Logic {
		
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
	    
	    
	    
	    private String[] rouletteNumbers = {"0", "32", "15", "19", "4", "21", "2", "25", "17", "34", "6", "27",
	                                        "13", "36", "11", "30", "8", "23", "10", "5", "24", "16", "33",
	                                        "1", "20", "14", "31", "9", "22", "18", "29", "7", "28", "12", "35",
	                                        "3", "26"};
	    
	    private String[] redNumbers = {"1", "3", "5", "7", "9", "12", "14", "16", "18", "19", "21", "23", 
	                                   "25", "27", "30", "32", "34", "36"};
	    private String[] column1_34 = {"1", "4", "7", "10", "13", "16", "19", "22", "25", "28", "31", "34"};
	    private String[] column2_35 = {"2", "5", "8", "11", "14", "17", "20", "23", "26", "29", "32", "35"};
	    private String[] column3_36 = {"3", "6", "9", "12", "15", "18", "21", "24", "27", "30", "33", "36"};
	    
	    
	    
	    
	    
	    private boolean readyState = false;
	    private double balance;
	    
	    
	    String[] playerOptions = {"Player 1", "Player 2", "Player 3", "Player 4"};
	    
	    ObjectId[] playerIds = {PLAYER_1_ID, PLAYER_2_ID, PLAYER_3_ID, PLAYER_4_ID};
	    private double[] balances = new double[4];
	    Timer ausgabe;
	    private View v;
	    private boolean solomode = true;
	    public Logic() {
	    	initMongoDB();
	    		
	    	GamePollingService service = new GamePollingService();
	    	startReadyCheckPolling();
	    	
	    	settingAllfalse();
        }
            
	    
	    
	    public void setView(View view) {
	    	this.v = view;
	    }
	    
	    public void soloMode(boolean solomode) {
	    	this.solomode = solomode;
	    }
	    
	    public void setPlayer(int player) {
	    	switch (player) {
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
	    }
	    
	    public ObjectId getPlayer(int index) {
	    	switch (index) {
            case 0:
                return PLAYER_1_ID;
            case 1:
                return PLAYER_2_ID;
            case 2:
                return PLAYER_3_ID;
            case 3:
                return PLAYER_4_ID;
	    	}
			return null;
	    }
	    
	    public double getBalance() {
	    	return balance;
	    }
	    	
	    public void setBalance(double balance) {
	    	this.balance = balance;
	    }
	    
	    
	    public void settingAllfalse() {
	    	if(selectedPlayer == PLAYER_1_ID) {
	    		updatePlayerData(PLAYER_1_ID, 1000, false);
	            updatePlayerData(PLAYER_2_ID, 1000, false);
	            updatePlayerData(PLAYER_3_ID, 1000, false);
	            updatePlayerData(PLAYER_4_ID, 1000, false);
	    	}
	    	if (solomode == true)
	    		balance = 1000;
	    	
	    	
	    }
	   
	  
	    
	    void initMongoDB() {
	        mongoClient = MongoClients.create(Config.MONGO_CONNECTION_STRING); 
	        database = mongoClient.getDatabase("Roulette");
	        players = database.getCollection("players");
	    }
	    
	    void updatePlayerData(ObjectId playerId, double change, boolean readyState) {
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
	    	if(!solomode) {
		        MongoCollection<Document> players = database.getCollection("players");
		        
		
		        for (int i = 0; i < playerIds.length; i++) {
		            Document playerDocument = players.find(eq("_id", playerIds[i])).first();
		            if (playerDocument != null) {
		                Double fetchedBalance = playerDocument.getDouble("balance");
		                if (fetchedBalance != null) {
		                    balances[i] = fetchedBalance;
		                    v.setPlayerBalances(balances);
		                    balance = balances[v.getPlayerSelectedIndex()];
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
	    
	    public double getPlayerBalances(int index) {
	    	return balances[index];
	    }
	    
	   


	    
	    public void startReadyCheckPolling() {
	    	if(!solomode) {
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
	                    v.spinRoulette(result);

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

	    public void dispose() {
	    	super.dispose();
	        if (mongoClient != null) {
	            mongoClient.close();
	        }
	    }

	   public String isRed(String number) {
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

	    public void randomRoll() {
	    	if (selectedPlayer == PLAYER_1_ID) {
		        final int position = (int) (Math.random() * rouletteNumbers.length);  
		        boolean resultLogged = false; 
		        if (!resultLogged) {
		            System.out.println("Random roll result: " + rouletteNumbers[position]);
		            
		            if (!solomode) {
		            	logGameResult(position);
		            } else {
		            	v.spinRoulette(position);
		            }
		            
	 
		            
		            resultLogged = true; 
		        }
	    	} else {
	    		System.out.println("Not all players are ready.");
	    	}
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

	   public void calculateBalance(String Eingabe, double bet, int resultIndex) {
	    	
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
			        	
			        }
			        if(!solomode) {
			        	updatePlayerData(selectedPlayer,balance, false);
			        }
			        v.drawPlayerInfo();
			        ausgabe.stop();
	    		}
	    	});
	        ausgabe.start();
	    }
	    
	   

		
	}

