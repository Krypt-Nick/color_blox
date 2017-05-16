package com.kurncz.rainblox.Models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by Administrator on 12/14/16.
 */

public class Session implements Parcelable {
    private int GameID;
    private boolean SecondPlayerConnected;
    private ImageButton[] GameBlocks;
    private boolean IsFinished;
    private int MoveCount;
    private int Winner;

    public Session (int gameID, boolean secondPlayerConnected, ImageButton[] gameBlocks)
    {
        this.GameID = gameID;
        this.SecondPlayerConnected = secondPlayerConnected;
        this.GameBlocks = gameBlocks;
    }

    public int getMoveCount() {
        return MoveCount;
    }

    public void setMoveCount(int moveCount) {
        MoveCount = moveCount;
    }

    public boolean isFinished() {
        return IsFinished;
    }

    public void setFinished(boolean finished) {
        IsFinished = finished;
    }


    public Session(Parcel in)
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
