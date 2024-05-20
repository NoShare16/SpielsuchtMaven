package com.spielsucht;

public class Player {
    private int playerId;
    private int balance;

    public Player(int playerId, int balance) {
        this.playerId = playerId;
        this.balance = balance;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Player " + playerId + ": Balance = €" + balance;
    }
}
