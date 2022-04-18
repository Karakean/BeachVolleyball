package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;


public class GameObject implements Serializable {

    @Getter
    protected int width;

    @Getter
    protected int height;

    @Getter
    protected Point coordinates;

    @Getter
    @Setter
    protected int velocityX;

    @Getter
    @Setter
    protected int velocityY;

    public void setCoordinateX(int x) {
        this.coordinates.x = x;
    }
    public void setCoordinateY(int y) {
        this.coordinates.y = y;
    }
}