package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

public class Player extends GameObject {

    int ID;
    @Getter
    @Setter
    protected boolean jump;

    public Player(int ID, int startX, int startY) {
        coordinates = new Point(startX, startY);
        this.ID = ID;
        width = 50;
        height = 75;
        velocityX = 0;
        velocityY = 0;
        jump = false;
    }

    @Override
    public String toString() {
        return "Player" + ID;
    }

}