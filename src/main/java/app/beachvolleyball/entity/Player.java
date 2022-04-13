package app.beachvolleyball.entity;

import java.awt.*;

public class Player extends GameObject{
    public Player(int startX, int startY) {
        coordinates = new Point(startX, startY);
        width = 50;
        height = 75;
        velocityX = 0;
        velocityY = 0;
    }
}