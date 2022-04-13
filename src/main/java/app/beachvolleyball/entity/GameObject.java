package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;


public class GameObject {

    @Getter
    protected int width;

    @Getter
    protected int height;

    @Getter
    @Setter
    protected Point coordinates;

    @Getter
    @Setter
    protected int velocityX;

    @Getter
    @Setter
    protected int velocityY;

}