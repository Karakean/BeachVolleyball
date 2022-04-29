package app.beachvolleyball.entity;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;


public class GameObject implements Serializable {

    protected static final long serialVersionUID = 13L;

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

    @Getter
    @Setter
    protected String imagePath;

    public void setCoordinateX(float x) {
        this.coordinates.x = Math.round(x);
    }
    public void setCoordinateY(float y) {
        this.coordinates.y = Math.round(y);
    }
}