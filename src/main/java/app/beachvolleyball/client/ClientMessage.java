package app.beachvolleyball.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
public class ClientMessage implements Serializable {

    static final long serialVersionUID = 2137L;

    @Getter
    @Setter
    private boolean dPressed;

    @Getter
    @Setter
    private boolean aPressed;

    @Getter
    @Setter
    private boolean spacePressed;


    @Getter
    @Setter
    private String textMessage;

}