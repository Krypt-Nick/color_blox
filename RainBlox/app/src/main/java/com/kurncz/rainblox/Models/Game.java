package com.kurncz.rainblox.Models;

import com.kurncz.rainblox.Helpers.Containts;

import java.util.List;

/**
 * Created by Administrator on 4/30/17.
 */

public class Game {
    private int NumPlayers;
    private int Turn;
    private int PlayerOneBlockLocked;
    private int PlayerTwoBlockLocked;
    private List<Integer> BlockColors;
    private boolean Finished;

    public Game(int numPlayers, int turn, int playerOneBlockLocked, int playerTwoBlockLocked, List<Integer> blockColors, boolean finished) {
        NumPlayers = numPlayers;
        Turn = turn;
        PlayerOneBlockLocked = playerOneBlockLocked;
        PlayerTwoBlockLocked = playerTwoBlockLocked;
        BlockColors = blockColors;
        Finished = finished;
    }

    public Game() {
    }

    public int getNumPlayers() {
        return NumPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        NumPlayers = numPlayers;
    }

    public int getTurn() {
        return Turn;
    }

    public void setTurn(int turn) {
        Turn = turn;
    }

    public int getPlayerOneBlockLocked() {
        return PlayerOneBlockLocked;
    }

    public void setPlayerOneBlockLocked(int playerOneBlockLocked) {
        PlayerOneBlockLocked = playerOneBlockLocked;
    }

    public int getPlayerTwoBlockLocked() {
        return PlayerTwoBlockLocked;
    }

    public void setPlayerTwoBlockLocked(int playerTwoBlockLocked) {
        PlayerTwoBlockLocked = playerTwoBlockLocked;
    }

    public List<Integer> getBlockColors() {
        return BlockColors;
    }

    public void setBlockColors(List<Integer> blockColors) {
        BlockColors = blockColors;
    }

    public boolean isFinished() {
        return Finished;
    }

    public void setFinished(boolean finished) {
        Finished = finished;
    }
}
