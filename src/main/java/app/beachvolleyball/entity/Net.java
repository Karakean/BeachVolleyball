package app.beachvolleyball.entity;

import java.awt.*;

public class Net extends GameObject{
    public Net(int x, int y) {
        coordinates = new Point(x, y);
        width = 20;
        height = y;
        velocityX = velocityY = 0;
    }
}