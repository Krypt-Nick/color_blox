package com.kurncz.rainblox.Models;

import android.graphics.Color;
import android.media.Image;
import android.widget.ImageButton;

/**
 * Created by Administrator on 12/14/16.
 */

public class Block {
    private int ID;
    private ImageButton BlockButton;

    public Block(int id, ImageButton blockButton)
    {
        this.ID = id;
        this.BlockButton = blockButton;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
