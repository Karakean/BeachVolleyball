package app.beachvolleyball.entity;

import java.awt.*;

public class Ball extends GameObject{
    public Ball(int x, int y) {
        coordinates = new Point(x, y);
        width = height = 10;
        velocityX = velocityY = 0;
    }
}