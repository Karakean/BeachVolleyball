package app.beachvolleyball.chat;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ChatController implements Serializable {

    @Setter
    private int clientID;

    @Getter
    @Setter
    private String currentMessage="";

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox messageBox;

    @FXML
    private TextField textField;


    public void sendMessage() {
        String tf = textField.getText();
        if (!tf.isEmpty()){
            currentMessage = "Player" + (clientID+1) + ": " + tf;
            textField.clear();
        }
    }

    public void receiveMessage(String message){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        hBox.getChildren().add(textFlow);
        messageBox.getChildren().add(hBox);
    }

}