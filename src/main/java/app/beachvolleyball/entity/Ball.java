package app.beachvolleyball.entity;

import javafx.scene.image.Image;

import java.awt.*;
import java.io.Serializable;

public class Ball extends GameObject {

    public Ball(int x, int y) {
        coordinates = new Point(x, y);
        width = height = 25;
        velocityX = velocityY = 0;
        imagePath = "/app/beachvolleyball/ball25px.png";
    }

}