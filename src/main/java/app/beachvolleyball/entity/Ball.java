package app.beachvolleyball.entity;

import java.awt.*;
import java.io.Serializable;

public class Ball extends GameObject {

    public Ball(int x, int y) {
        coordinates = new Point(x, y);
        width = height = 10;
        velocityX = velocityY = 0;
    }

}