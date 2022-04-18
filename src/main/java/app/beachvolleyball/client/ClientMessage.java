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
    private String key;

    @Getter
    @Setter
    private boolean isPressed;

    @Getter
    @Setter
    private String textMessage;

}