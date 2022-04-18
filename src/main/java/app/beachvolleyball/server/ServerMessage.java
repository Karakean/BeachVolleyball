package app.beachvolleyball.server;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.Serializable;

@AllArgsConstructor
public class ServerMessage implements Serializable {

    static final long serialVersionUID = 999999000001L;

    @Getter
    @Setter
    private Point player1Position;

    @Getter
    @Setter
    private Point player2Position;

    @Getter
    @Setter
    private String verifiedMessage;

}