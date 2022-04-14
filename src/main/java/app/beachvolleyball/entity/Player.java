package app.beachvolleyball.entity;

import java.awt.*;

public class Player extends GameObject{

    int ID;

    public Player(int ID, int startX, int startY) {
        coordinates = new Point(startX, startY);
        this.ID = ID;
        width = 50;
        height = 75;
        velocityX = 0;
        velocityY = 0;
    }

    @Override
    public String toString() {
        return "Player" + ID;
    }

}