package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serial;

public class Player extends GameObject {

    @Serial
    private static final long serialVersionUID = 13L;


    @Getter
    @Setter
    private boolean jump;


    public Player(int startX, int startY, int playerID) {
        coordinates = new Point(startX, startY);
        width = 50;
        height = 75;
        velocityX = 0;
        velocityY = 0;
        jump = false;
        if(playerID == 1)
            imagePath = "/app/beachvolleyball/red.png";
        else
            imagePath = "/app/beachvolleyball/green.png";
    }
}