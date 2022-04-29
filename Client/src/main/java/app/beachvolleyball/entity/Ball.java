package app.beachvolleyball.entity;

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public class Ball extends GameObject {

    @Serial
    private static final long serialVersionUID = 13L;


    public Ball(int x, int y) {
        coordinates = new Point(x, y);
        width = height = 25;
        velocityX = velocityY = 0;
        imagePath = "/app/beachvolleyball/ball25px.png";
    }

}