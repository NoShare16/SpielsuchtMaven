package com.spielsucht;

import org.bson.types.ObjectId;

public class Player {
    private ObjectId playerId;
    private double balance;
    private boolean readyState;

    // Constructor that matches the required signature
    public Player(ObjectId playerId, double balance, boolean readyState) {
        this.playerId = playerId;
        this.balance = balance;
        this.readyState = readyState;
    }

    // Getter and setter methods
    public ObjectId getPlayerId() {
        return playerId;
    }

    public void setPlayerId(ObjectId playerId) {
        this.playerId = playerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isReadyState() {
        return readyState;
    }

    public void setReadyState(boolean readyState) {
        this.readyState = readyState;
    }

    // ToString method for debugging
    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", balance=" + balance +
                ", readyState=" + readyState +
                '}';
    }
}
