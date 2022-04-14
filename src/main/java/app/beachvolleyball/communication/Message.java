package app.beachvolleyball.communication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
public class Message implements Serializable {

    static final long serialVersionUID = 999999000001L;

    @Getter
    @Setter
    private String key;

}