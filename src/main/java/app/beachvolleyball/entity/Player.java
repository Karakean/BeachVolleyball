package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Player extends GameObject {

    @Getter
    @Setter
    private boolean jump;

    public Player(int startX, int startY) {
        coordinates = new Point(startX, startY);
        width = 50;
        height = 75;
        velocityX = 0;
        velocityY = 0;
        jump = false;
    }
}