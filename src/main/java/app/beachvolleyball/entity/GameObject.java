package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;


public class GameObject implements Serializable {

    @Getter
    protected float width;

    @Getter
    protected float height;

    @Getter
    protected Point coordinates;

    @Getter
    @Setter
    protected float velocityX;

    @Getter
    @Setter
    protected float velocityY;

    public void setCoordinateX(float x) {
        this.coordinates.x = Math.round(x);
    }
    public void setCoordinateY(float y) {
        this.coordinates.y = Math.round(y);
    }
}