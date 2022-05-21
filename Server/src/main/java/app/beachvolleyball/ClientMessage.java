package app.beachvolleyball;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
public class ClientMessage implements Serializable {

    static final long serialVersionUID = 2137L;

    @Getter
    @Setter
    private boolean left;

    @Getter
    @Setter
    private boolean right;

    @Getter
    @Setter
    private boolean jump;


    @Getter
    @Setter
    private String textMessage;

}