package app.beachvolleyball.entity;

import java.awt.*;
import java.io.Serial;

public class Net extends GameObject{

    @Serial
    private static final long serialVersionUID = 13L;


    public Net(int x, int y) {
        coordinates = new Point(x, y);
        width = 20;
        height = y;
        velocityX = velocityY = 0;
    }
}